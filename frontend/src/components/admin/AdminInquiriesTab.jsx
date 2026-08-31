import { useEffect, useState } from 'react'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { api } from '@/lib/api'
import { INQUIRY_STATUS_LABELS, INQUIRY_TYPE_LABELS } from '@/lib/constants'

const STATUS_OPTIONS = Object.keys(INQUIRY_STATUS_LABELS)

export function AdminInquiriesTab() {
  const [status, setStatus] = useState('RECEIVED')
  const [inquiries, setInquiries] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  function load() {
    setLoading(true)
    api
      .get('/inquiries', { params: { status } })
      .then((res) => setInquiries(res.data))
      .catch((err) => setError(err.response?.data?.message ?? '문의 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [status])

  async function handleChangeStatus(inquiryNo, nextStatus) {
    try {
      await api.patch(`/inquiries/${inquiryNo}/status`, { processStatus: nextStatus })
      load()
    } catch (err) {
      setError(err.response?.data?.message ?? '상태 변경에 실패했습니다.')
    }
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="flex justify-end">
        <Select value={status} onValueChange={setStatus}>
          <SelectTrigger className="w-40">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            {STATUS_OPTIONS.map((value) => (
              <SelectItem key={value} value={value}>
                {INQUIRY_STATUS_LABELS[value]}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
      </div>

      {loading && <p className="text-muted-foreground">불러오는 중...</p>}
      {!loading && error && <p className="text-destructive">{error}</p>}
      {!loading && !error && inquiries.length === 0 && (
        <p className="py-6 text-center text-sm text-muted-foreground">
          해당 상태의 문의가 없습니다.
        </p>
      )}

      {!loading &&
        !error &&
        inquiries.map((inquiry) => (
          <Card key={inquiry.inquiryNo}>
            <CardContent className="flex flex-col gap-2 text-sm">
              <div className="flex items-center justify-between">
                <Badge variant="secondary">
                  {INQUIRY_TYPE_LABELS[inquiry.inquiryType] ?? inquiry.inquiryType}
                </Badge>
                <span className="text-muted-foreground">
                  회원번호 {inquiry.memberNo} · {inquiry.inquiryDate}
                </span>
              </div>
              <p>{inquiry.inquiryContent}</p>
              <div className="flex justify-end gap-2 pt-2">
                {STATUS_OPTIONS.filter((value) => value !== inquiry.processStatus).map((value) => (
                  <Button
                    key={value}
                    size="sm"
                    variant="outline"
                    onClick={() => handleChangeStatus(inquiry.inquiryNo, value)}
                  >
                    {INQUIRY_STATUS_LABELS[value]}(으)로 변경
                  </Button>
                ))}
              </div>
            </CardContent>
          </Card>
        ))}
    </div>
  )
}
