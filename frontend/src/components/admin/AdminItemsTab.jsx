import { useEffect, useState } from 'react'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { api } from '@/lib/api'
import { CATEGORY_LABELS, ITEM_STATUS_LABELS } from '@/lib/constants'

export function AdminItemsTab() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  function load() {
    setLoading(true)
    api
      .get('/items')
      .then((res) => setItems(res.data))
      .catch((err) => setError(err.response?.data?.message ?? '물품 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  async function handleToggleStatus(item) {
    const nextStatus = item.itemStatus === 'AVAILABLE' ? 'UNAVAILABLE' : 'AVAILABLE'
    try {
      await api.patch(`/admin/items/${item.itemNo}/status`, null, { params: { status: nextStatus } })
      load()
    } catch (err) {
      setError(err.response?.data?.message ?? '상태 변경에 실패했습니다.')
    }
  }

  async function handleDelete(item) {
    if (!window.confirm(`"${item.itemName}" 물품을 삭제하시겠습니까?`)) {
      return
    }
    try {
      await api.delete(`/admin/items/${item.itemNo}`)
      load()
    } catch (err) {
      setError(err.response?.data?.message ?? '삭제에 실패했습니다.')
    }
  }

  if (loading) {
    return <p className="text-muted-foreground">불러오는 중...</p>
  }

  if (error) {
    return <p className="text-destructive">{error}</p>
  }

  return (
    <div className="flex flex-col gap-2">
      {items.map((item) => (
        <Card key={item.itemNo}>
          <CardContent className="flex items-center justify-between text-sm">
            <div>
              <p className="font-medium">{item.itemName}</p>
              <p className="text-muted-foreground">
                {CATEGORY_LABELS[item.category] ?? item.category} · {item.ownerNickname} ·{' '}
                {item.rentalPoint.toLocaleString()}P
              </p>
            </div>
            <div className="flex items-center gap-2">
              <Badge variant={item.itemStatus === 'AVAILABLE' ? 'secondary' : 'destructive'}>
                {ITEM_STATUS_LABELS[item.itemStatus] ?? item.itemStatus}
              </Badge>
              <Button size="sm" variant="outline" onClick={() => handleToggleStatus(item)}>
                {item.itemStatus === 'AVAILABLE' ? '비활성화' : '활성화'}
              </Button>
              <Button size="sm" variant="destructive" onClick={() => handleDelete(item)}>
                삭제
              </Button>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
