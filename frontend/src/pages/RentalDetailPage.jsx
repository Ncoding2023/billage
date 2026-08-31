import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { api } from '@/lib/api'
import { useAuth } from '@/lib/auth'
import { RENTAL_STATUS_LABELS, RETURN_STATUS_LABELS } from '@/lib/constants'

export function RentalDetailPage() {
  const { rentalNo } = useParams()
  const { member } = useAuth()
  const [rental, setRental] = useState(null)
  const [item, setItem] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [actionLoading, setActionLoading] = useState(false)
  const [actionError, setActionError] = useState('')
  const [returnStatus, setReturnStatus] = useState('NORMAL')
  const [returnMemo, setReturnMemo] = useState('')

  async function load() {
    setLoading(true)
    setError('')
    try {
      const { data: rentalData } = await api.get(`/rentals/${rentalNo}`)
      setRental(rentalData)
      const { data: itemData } = await api.get(`/items/${rentalData.itemNo}`)
      setItem(itemData)
    } catch (err) {
      setError(err.response?.data?.message ?? '대여 정보를 불러오지 못했습니다.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [rentalNo])

  async function runAction(action) {
    setActionError('')
    setActionLoading(true)
    try {
      await action()
      await load()
    } catch (err) {
      setActionError(err.response?.data?.message ?? '처리에 실패했습니다.')
    } finally {
      setActionLoading(false)
    }
  }

  function handleStart() {
    runAction(() => api.patch(`/rentals/${rentalNo}/start`))
  }

  function handleCancel() {
    if (!window.confirm('대여를 취소하시겠습니까? 사용된 포인트는 환불됩니다.')) {
      return
    }
    runAction(() => api.patch(`/rentals/${rentalNo}/cancel`))
  }

  function handleRequestReturn() {
    runAction(() => api.patch(`/rentals/${rentalNo}/return`))
  }

  function handleConfirmReturn() {
    runAction(() =>
      api.patch(`/rentals/${rentalNo}/return/confirm`, {
        returnStatus,
        returnMemo: returnMemo || null,
      }),
    )
  }

  if (loading) {
    return <p className="text-muted-foreground">불러오는 중...</p>
  }

  if (error) {
    return <p className="text-destructive">{error}</p>
  }

  if (!rental || !item) {
    return null
  }

  const isRenter = member?.memberNo === rental.memberNo
  const isProvider = member?.memberNo === item.memberNo

  return (
    <div className="mx-auto max-w-lg">
      <Card>
        <CardHeader>
          <CardTitle>대여 정보</CardTitle>
          <CardDescription>
            <Link to={`/items/${rental.itemNo}`} className="underline underline-offset-4">
              {rental.itemName}
            </Link>
          </CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-3 text-sm">
          <Row label="상태" value={RENTAL_STATUS_LABELS[rental.rentalStatus] ?? rental.rentalStatus} />
          <Row label="대여 기간" value={`${rental.rentalStartDate} ~ ${rental.rentalEndDate}`} />
          <Row label="대여 장소" value={`${rental.rentalPlaceName} (${rental.rentalPlace})`} />
          {rental.rentalPlaceDetail && <Row label="상세 주소" value={rental.rentalPlaceDetail} />}
          <Row label="이용자" value={rental.memberNickname} />
          <Row label="제공자" value={item.ownerNickname} />
          <Row label="사용 포인트" value={`${rental.rentalPoint.toLocaleString()}P`} />

          {actionError && <p className="text-sm text-destructive">{actionError}</p>}

          {rental.rentalStatus === 'REQUESTED' && isProvider && (
            <Button onClick={handleStart} disabled={actionLoading} className="w-full">
              {actionLoading ? '처리 중...' : '대여 시작 확인'}
            </Button>
          )}

          {rental.rentalStatus === 'REQUESTED' && isRenter && (
            <Button
              variant="destructive"
              onClick={handleCancel}
              disabled={actionLoading}
              className="w-full"
            >
              {actionLoading ? '처리 중...' : '대여 취소'}
            </Button>
          )}

          {rental.rentalStatus === 'RENTING' && isRenter && (
            <Button onClick={handleRequestReturn} disabled={actionLoading} className="w-full">
              {actionLoading ? '처리 중...' : '반납 신청'}
            </Button>
          )}

          {rental.rentalStatus === 'RETURN_PENDING' && isProvider && (
            <div className="flex flex-col gap-3 border-t pt-4">
              <p className="font-medium">반납 확인</p>
              <Select value={returnStatus} onValueChange={setReturnStatus}>
                <SelectTrigger className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {Object.entries(RETURN_STATUS_LABELS).map(([value, label]) => (
                    <SelectItem key={value} value={value}>
                      {label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <Textarea
                placeholder="반납 메모 (선택)"
                value={returnMemo}
                onChange={(e) => setReturnMemo(e.target.value)}
                rows={3}
              />
              <Button onClick={handleConfirmReturn} disabled={actionLoading} className="w-full">
                {actionLoading ? '처리 중...' : '반납 확인 완료'}
              </Button>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

function Row({ label, value }) {
  return (
    <div className="flex items-center justify-between border-b pb-2 last:border-b-0">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium">{value}</span>
    </div>
  )
}
