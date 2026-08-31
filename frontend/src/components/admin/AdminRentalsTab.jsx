import { useEffect, useState } from 'react'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent } from '@/components/ui/card'
import { api } from '@/lib/api'
import { RENTAL_STATUS_LABELS } from '@/lib/constants'

export function AdminRentalsTab() {
  const [rentals, setRentals] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    api
      .get('/admin/rentals')
      .then((res) => setRentals(res.data))
      .catch((err) => setError(err.response?.data?.message ?? '대여 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }, [])

  if (loading) {
    return <p className="text-muted-foreground">불러오는 중...</p>
  }

  if (error) {
    return <p className="text-destructive">{error}</p>
  }

  return (
    <div className="flex flex-col gap-2">
      {rentals.map((rental) => (
        <Card key={rental.rentalNo}>
          <CardContent className="flex items-center justify-between text-sm">
            <div>
              <p className="font-medium">{rental.itemName}</p>
              <p className="text-muted-foreground">
                {rental.memberNickname} · {rental.rentalStartDate} ~ {rental.rentalEndDate} ·{' '}
                {rental.rentalPoint.toLocaleString()}P
              </p>
            </div>
            <Badge variant="secondary">
              {RENTAL_STATUS_LABELS[rental.rentalStatus] ?? rental.rentalStatus}
            </Badge>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
