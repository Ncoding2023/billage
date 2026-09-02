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
import { getItemImageSrc } from '@/lib/categoryImages'
import { CATEGORY_LABELS } from '@/lib/constants'

const ALL_CATEGORIES = '__all__'
const CATEGORY_ORDER = Object.keys(CATEGORY_LABELS)
const PAGE_SIZE = 10

export function ItemListPage() {
  const [category, setCategory] = useState(ALL_CATEGORIES)
  const [keyword, setKeyword] = useState('')
  const [debouncedKeyword, setDebouncedKeyword] = useState('')

  useEffect(() => {
    const timer = setTimeout(() => setDebouncedKeyword(keyword.trim()), 300)
    return () => clearTimeout(timer)
  }, [keyword])

  const showAll = category === ALL_CATEGORIES
  const visibleCategories = showAll
    ? CATEGORY_ORDER
    : CATEGORY_ORDER.filter((key) => key === category)

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

      <div className="flex flex-col gap-8">
        {visibleCategories.map((key) => (
          <CategoryCarouselRow
            key={`${key}-${debouncedKeyword}`}
            categoryKey={key}
            label={CATEGORY_LABELS[key]}
            keyword={debouncedKeyword}
            hideWhenEmpty={showAll}
          />
        ))}
      </div>
    </div>
  )
}

function CategoryCarouselRow({ categoryKey, label, keyword, hideWhenEmpty }) {
  const scrollerRef = useRef(null)
  const cardRefs = useRef([])
  const [page, setPage] = useState(0)
  const [activeIndex, setActiveIndex] = useState(0)
  const [items, setItems] = useState([])
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    setLoading(true)
    setError('')
    const params = {
      category: categoryKey,
      page,
      size: PAGE_SIZE,
    }
    if (keyword) {
      params.keyword = keyword
    }

    let cancelled = false
    api
      .get('/items', { params })
      .then((res) => {
        if (cancelled) {
          return
        }
        const content = res.data.content ?? []
        setItems(content)
        setTotalPages(res.data.totalPages ?? 0)
        setTotalElements(res.data.totalElements ?? 0)
        setActiveIndex(0)
        requestAnimationFrame(() => {
          scrollerRef.current?.scrollTo({ left: 0, behavior: 'smooth' })
        })
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err.response?.data?.message ?? '물품을 불러오지 못했습니다.')
        }
      })
      .finally(() => {
        if (!cancelled) {
          setLoading(false)
        }
      })

    return () => {
      cancelled = true
    }
  }, [categoryKey, keyword, page])

  function scrollToIndex(index) {
    setActiveIndex(index)
    cardRefs.current[index]?.scrollIntoView({
      behavior: 'smooth',
      inline: 'start',
      block: 'nearest',
    })
  }

  function goPrevItem() {
    if (loading || activeIndex <= 0) {
      return
    }
    scrollToIndex(activeIndex - 1)
  }

  function goNextItem() {
    if (loading || activeIndex >= items.length - 1) {
      return
    }
    scrollToIndex(activeIndex + 1)
  }

  function goPrevPage() {
    if (loading || page <= 0) {
      return
    }
    setPage((prev) => prev - 1)
  }

  function goNextPage() {
    if (loading || page >= totalPages - 1) {
      return
    }
    setPage((prev) => prev + 1)
  }

  if (!loading && !error && totalElements === 0) {
    if (hideWhenEmpty) {
      return null
    }
    return <p className="text-muted-foreground">조건에 맞는 물품이 없습니다.</p>
  }

  return (
    <section className="flex flex-col gap-3">
      <div className="flex items-center justify-between gap-3">
        <h2 className="text-lg font-semibold tracking-tight">
          {label}
          <span className="ml-2 text-sm font-normal text-muted-foreground">
            {totalElements}
          </span>
        </h2>
        {totalPages > 1 && (
          <div className="flex items-center gap-1">
            <Button
              type="button"
              variant="outline"
              size="icon-sm"
              aria-label={`${label} 이전 페이지`}
              disabled={page <= 0 || loading}
              onClick={goPrevPage}
            >
              <ChevronLeft />
            </Button>
            <span className="min-w-14 text-center text-xs text-muted-foreground">
              {page + 1} / {totalPages}
            </span>
            <Button
              type="button"
              variant="outline"
              size="icon-sm"
              aria-label={`${label} 다음 페이지`}
              disabled={page >= totalPages - 1 || loading}
              onClick={goNextPage}
            >
              <ChevronRight />
            </Button>
          </div>
        )}
      </div>

      {loading && <p className="text-sm text-muted-foreground">불러오는 중...</p>}
      {!loading && error && <p className="text-sm text-destructive">{error}</p>}

      {!loading && !error && items.length > 0 && (
        <div className="flex items-center gap-2">
          {items.length > 1 && (
            <Button
              type="button"
              variant="outline"
              size="icon-sm"
              aria-label={`${label} 이전 물품`}
              disabled={activeIndex <= 0}
              onClick={goPrevItem}
              className="shrink-0 disabled:opacity-40"
            >
              <ChevronLeft />
            </Button>
          )}

          <div
            ref={scrollerRef}
            className="flex min-w-0 flex-1 gap-3 overflow-x-auto pb-2 scroll-smooth snap-x snap-mandatory [scrollbar-width:thin]"
          >
            {items.map((item, index) => (
              <Link
                key={item.itemNo}
                ref={(el) => {
                  cardRefs.current[index] = el
                }}
                to={`/items/${item.itemNo}`}
                className="w-56 shrink-0 snap-start"
              >
                <Card className="h-full overflow-hidden transition-colors hover:bg-muted/50">
                  <img
                    src={getItemImageSrc(item.category, item.mainImagePath)}
                    alt={item.itemName}
                    className="aspect-[4/3] w-full object-cover"
                  />
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

          {items.length > 1 && (
            <Button
              type="button"
              variant="outline"
              size="icon-sm"
              aria-label={`${label} 다음 물품`}
              disabled={activeIndex >= items.length - 1}
              onClick={goNextItem}
              className="shrink-0 disabled:opacity-40"
            >
              <ChevronRight />
            </Button>
          )}
        </div>
      )}
    </section>
  )
}
