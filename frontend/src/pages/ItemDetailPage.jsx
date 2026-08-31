import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { Button, buttonVariants } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { ItemImageManager } from '@/components/ItemImageManager'
import { KakaoMap } from '@/components/KakaoMap'
import { cn } from '@/lib/utils'
import { API_ORIGIN, api } from '@/lib/api'
import { useAuth } from '@/lib/auth'
import { CATEGORY_LABELS, ITEM_STATUS_LABELS } from '@/lib/constants'

export function ItemDetailPage() {
  const { itemNo } = useParams()
  const navigate = useNavigate()
  const { member } = useAuth()
  const [item, setItem] = useState(null)
  const [images, setImages] = useState([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [deleting, setDeleting] = useState(false)

  function loadImages() {
    return api.get(`/items/${itemNo}/images`).then((res) => setImages(res.data))
  }

  useEffect(() => {
    setLoading(true)
    setError('')
    Promise.all([api.get(`/items/${itemNo}`), loadImages()])
      .then(([itemRes]) => setItem(itemRes.data))
      .catch((err) =>
        setError(err.response?.data?.message ?? '물품 정보를 불러오지 못했습니다.'),
      )
      .finally(() => setLoading(false))
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [itemNo])

  async function handleDelete() {
    if (!window.confirm('정말 이 물품을 삭제하시겠습니까?')) {
      return
    }
    setDeleting(true)
    try {
      await api.delete(`/items/${itemNo}`)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.message ?? '물품 삭제에 실패했습니다.')
      setDeleting(false)
    }
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

  const isOwner = member?.memberNo === item.memberNo
  const mainImage = images.find((img) => img.mainImage) ?? images[0]

  return (
    <div className="mx-auto max-w-2xl">
      <Card>
        <CardContent className="flex flex-col gap-4">
          {mainImage ? (
            <img
              src={`${API_ORIGIN}${mainImage.imagePath}`}
              alt={item.itemName}
              className="aspect-video w-full rounded-lg object-cover"
            />
          ) : (
            <div className="flex aspect-video w-full items-center justify-center rounded-lg bg-muted text-sm text-muted-foreground">
              등록된 이미지가 없습니다
            </div>
          )}

          <div className="flex items-start justify-between">
            <div>
              <p className="text-sm text-muted-foreground">
                {CATEGORY_LABELS[item.category] ?? item.category}
              </p>
              <h1 className="text-2xl font-semibold">{item.itemName}</h1>
            </div>
            <span className="rounded-full bg-muted px-3 py-1 text-xs text-muted-foreground">
              {ITEM_STATUS_LABELS[item.itemStatus] ?? item.itemStatus}
            </span>
          </div>

          <p className="whitespace-pre-wrap text-sm text-foreground">
            {item.description || '등록된 설명이 없습니다.'}
          </p>

          <div className="flex items-center justify-between border-t pt-4 text-sm">
            <span className="text-muted-foreground">
              등록자 {item.ownerNickname}
            </span>
            <span className="text-lg font-semibold">
              {item.rentalPoint.toLocaleString()}P
            </span>
          </div>

          <div className="border-t pt-4 text-sm">
            <p className="mb-1 font-medium">거래 장소</p>
            <p className="mb-3 text-muted-foreground">
              {item.rentalPlaceName} · {item.rentalPlace}
              {item.rentalPlaceDetail && ` (${item.rentalPlaceDetail})`}
            </p>
            <KakaoMap latitude={item.latitude} longitude={item.longitude} />
          </div>

          {isOwner && (
            <ItemImageManager itemNo={itemNo} images={images} onChanged={loadImages} />
          )}

          {isOwner ? (
            <div className="flex justify-end gap-2 border-t pt-4">
              <Link
                to={`/items/${itemNo}/edit`}
                className={cn(buttonVariants({ variant: 'outline' }))}
              >
                수정
              </Link>
              <Button
                variant="destructive"
                onClick={handleDelete}
                disabled={deleting}
              >
                {deleting ? '삭제 중...' : '삭제'}
              </Button>
            </div>
          ) : (
            <div className="border-t pt-4">
              {!member ? (
                <Link
                  to="/login"
                  className={cn(buttonVariants({ size: 'lg' }), 'w-full')}
                >
                  로그인하고 대여 신청하기
                </Link>
              ) : item.itemStatus !== 'AVAILABLE' ? (
                <Button size="lg" className="w-full" disabled>
                  현재 대여할 수 없는 물품입니다
                </Button>
              ) : (
                <Link
                  to={`/items/${itemNo}/rent`}
                  className={cn(buttonVariants({ size: 'lg' }), 'w-full')}
                >
                  대여 신청하기
                </Link>
              )}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
