import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import { Button } from '@/components/ui/button'
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
const CATEGORY_ORDER = Object.keys(CATEGORY_LABELS)
const CARD_SCROLL_OFFSET = 280

function groupItemsByCategory(items, selectedCategory) {
  const grouped = Object.fromEntries(CATEGORY_ORDER.map((key) => [key, []]))
  for (const item of items) {
    if (grouped[item.category]) {
      grouped[item.category].push(item)
    }
  }

  const keys =
    selectedCategory === ALL_CATEGORIES
      ? CATEGORY_ORDER
      : CATEGORY_ORDER.filter((key) => key === selectedCategory)

  return keys
    .map((key) => ({
      key,
      label: CATEGORY_LABELS[key],
      items: grouped[key] ?? [],
    }))
    .filter((row) => row.items.length > 0)
}

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

  const categoryRows = groupItemsByCategory(items, category)

  return (
    <div className="flex flex-col gap-8">
      <div className="flex flex-col gap-3 sm:flex-row">
        <Select value={category} onValueChange={setCategory}>
          <SelectTrigger className="w-full sm:w-48">
            <SelectValue>
              {(value) =>
                value === ALL_CATEGORIES
                  ? '전체'
                  : (CATEGORY_LABELS[value] ?? value)
              }
            </SelectValue>
          </SelectTrigger>
          <SelectContent>
            <SelectItem value={ALL_CATEGORIES}>전체</SelectItem>
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
      {!loading && !error && categoryRows.length === 0 && (
        <p className="text-muted-foreground">조건에 맞는 물품이 없습니다.</p>
      )}

      {!loading && !error && categoryRows.length > 0 && (
        <div className="flex flex-col gap-8">
          {categoryRows.map((row) => (
            <CategoryCarouselRow key={row.key} label={row.label} items={row.items} />
          ))}
        </div>
      )}
    </div>
  )
}

function CategoryCarouselRow({ label, items }) {
  const scrollerRef = useRef(null)

  function scrollBy(direction) {
    scrollerRef.current?.scrollBy({
      left: direction * CARD_SCROLL_OFFSET,
      behavior: 'smooth',
    })
  }

  return (
    <section className="flex flex-col gap-3">
      <div className="flex items-center justify-between gap-3">
        <h2 className="text-lg font-semibold tracking-tight">
          {label}
          <span className="ml-2 text-sm font-normal text-muted-foreground">
            {items.length}
          </span>
        </h2>
        <div className="flex gap-1">
          <Button
            type="button"
            variant="outline"
            size="icon-sm"
            aria-label={`${label} 이전`}
            onClick={() => scrollBy(-1)}
          >
            <ChevronLeft />
          </Button>
          <Button
            type="button"
            variant="outline"
            size="icon-sm"
            aria-label={`${label} 다음`}
            onClick={() => scrollBy(1)}
          >
            <ChevronRight />
          </Button>
        </div>
      </div>

      <div
        ref={scrollerRef}
        className="-mx-1 flex gap-3 overflow-x-auto px-1 pb-2 scroll-smooth snap-x snap-mandatory [scrollbar-width:thin]"
      >
        {items.map((item) => (
          <Link
            key={item.itemNo}
            to={`/items/${item.itemNo}`}
            className="w-56 shrink-0 snap-start"
          >
            <Card className="h-full transition-colors hover:bg-muted/50">
              <CardHeader>
                <CardTitle className="line-clamp-2">{item.itemName}</CardTitle>
              </CardHeader>
              <CardContent className="flex flex-col gap-1 text-sm text-muted-foreground">
                <span>{item.rentalPoint.toLocaleString()}P / 대여</span>
                <span>{item.ownerNickname}</span>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>
    </section>
  )
}
