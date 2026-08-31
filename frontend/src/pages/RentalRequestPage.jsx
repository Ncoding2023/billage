import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { api } from '@/lib/api'

export function RentalRequestPage() {
  const { itemNo } = useParams()
  const navigate = useNavigate()
  const [item, setItem] = useState(null)
  const [rentalStartDate, setRentalStartDate] = useState('')
  const [rentalEndDate, setRentalEndDate] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    api
      .get(`/items/${itemNo}`)
      .then((res) => setItem(res.data))
      .catch((err) => setError(err.response?.data?.message ?? '물품 정보를 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }, [itemNo])

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    if (rentalEndDate < rentalStartDate) {
      setError('종료일은 시작일보다 빠를 수 없습니다.')
      return
    }

    setSubmitting(true)
    try {
      const { data } = await api.post('/rentals', {
        itemNo: Number(itemNo),
        rentalStartDate,
        rentalEndDate,
      })
      navigate(`/rentals/${data.rentalNo}`)
    } catch (err) {
      setError(err.response?.data?.message ?? '대여 신청에 실패했습니다.')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) {
    return <p className="text-muted-foreground">불러오는 중...</p>
  }

  if (!item) {
    return <p className="text-destructive">{error}</p>
  }

  return (
    <div className="mx-auto max-w-lg">
      <Card>
        <CardHeader>
          <CardTitle>대여 신청</CardTitle>
          <CardDescription>
            {item.itemName} · {item.rentalPoint.toLocaleString()}P
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="mb-4 rounded-lg bg-muted p-3 text-sm">
            <p className="font-medium">거래 장소</p>
            <p className="text-muted-foreground">
              {item.rentalPlaceName} · {item.rentalPlace}
              {item.rentalPlaceDetail && ` (${item.rentalPlaceDetail})`}
            </p>
          </div>

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="flex flex-col gap-1.5">
                <Label htmlFor="rentalStartDate">대여 시작일</Label>
                <Input
                  id="rentalStartDate"
                  type="date"
                  value={rentalStartDate}
                  onChange={(e) => setRentalStartDate(e.target.value)}
                  required
                />
              </div>
              <div className="flex flex-col gap-1.5">
                <Label htmlFor="rentalEndDate">대여 종료일</Label>
                <Input
                  id="rentalEndDate"
                  type="date"
                  value={rentalEndDate}
                  onChange={(e) => setRentalEndDate(e.target.value)}
                  required
                />
              </div>
            </div>
            {error && <p className="text-sm text-destructive">{error}</p>}
            <Button type="submit" disabled={submitting} className="w-full">
              {submitting ? '신청 중...' : `${item.rentalPoint.toLocaleString()}P로 대여 신청`}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
