import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { ItemForm } from '@/components/ItemForm'
import { api } from '@/lib/api'
import { useAuth } from '@/lib/auth'

export function ItemEditPage() {
  const { itemNo } = useParams()
  const navigate = useNavigate()
  const { member } = useAuth()
  const [item, setItem] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api
      .get(`/items/${itemNo}`)
      .then((res) => setItem(res.data))
      .catch((err) => setError(err.response?.data?.message ?? '물품 정보를 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }, [itemNo])

  async function handleSubmit(payload) {
    await api.patch(`/items/${itemNo}`, payload)
    navigate(`/items/${itemNo}`)
  }

  if (loading) {
    return <p className="text-muted-foreground">불러오는 중...</p>
  }

  if (error) {
    return <p className="text-destructive">{error}</p>
  }

  if (!item) {
    return null
  }

  if (member?.memberNo !== item.memberNo) {
    return <p className="text-destructive">본인이 등록한 물품만 수정할 수 있습니다.</p>
  }

  return (
    <div className="mx-auto max-w-lg">
      <Card>
        <CardHeader>
          <CardTitle>물품 수정</CardTitle>
          <CardDescription>물품 정보와 거래 장소를 수정해주세요.</CardDescription>
        </CardHeader>
        <CardContent>
          <ItemForm initial={item} onSubmit={handleSubmit} submitLabel="수정하기" />
        </CardContent>
      </Card>
    </div>
  )
}
