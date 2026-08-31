import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { api } from '@/lib/api'
import { CATEGORY_LABELS } from '@/lib/constants'

const ALL_CATEGORIES = '__all__'

export function ItemListPage() {
  const [items, setItems] = useState([])
  const [category, setCategory] = useState(ALL_CATEGORIES)
  const [keyword, setKeyword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    const params = {}
    if (category !== ALL_CATEGORIES) {
      params.category = category
    }
    if (keyword.trim()) {
      params.keyword = keyword.trim()
    }

    const timer = setTimeout(() => {
      api
        .get('/items', { params })
        .then((res) => setItems(res.data))
        .catch((err) => setError(err.response?.data?.message ?? '물품을 불러오지 못했습니다.'))
        .finally(() => setLoading(false))
    }, 300)

    return () => clearTimeout(timer)
  }, [category, keyword])

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col gap-3 sm:flex-row">
        <Select value={category} onValueChange={setCategory}>
          <SelectTrigger className="w-full sm:w-48">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value={ALL_CATEGORIES}>전체 카테고리</SelectItem>
            {Object.entries(CATEGORY_LABELS).map(([value, label]) => (
              <SelectItem key={value} value={value}>
                {label}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        <Input
          placeholder="물품명으로 검색"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className="flex-1"
        />
      </div>

      {loading && <p className="text-muted-foreground">불러오는 중...</p>}
      {!loading && error && <p className="text-destructive">{error}</p>}
      {!loading && !error && items.length === 0 && (
        <p className="text-muted-foreground">조건에 맞는 물품이 없습니다.</p>
      )}

      {!loading && !error && items.length > 0 && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3">
          {items.map((item) => (
            <Link key={item.itemNo} to={`/items/${item.itemNo}`}>
              <Card className="h-full transition-colors hover:bg-muted/50">
                <CardHeader>
                  <CardTitle>{item.itemName}</CardTitle>
                </CardHeader>
                <CardContent className="flex flex-col gap-1 text-sm text-muted-foreground">
                  <span>{CATEGORY_LABELS[item.category] ?? item.category}</span>
                  <span>{item.rentalPoint.toLocaleString()}P / 대여</span>
                  <span>{item.ownerNickname}</span>
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
