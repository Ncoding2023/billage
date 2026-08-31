import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Label } from '@/components/ui/label'
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
import { INQUIRY_TYPE_LABELS } from '@/lib/constants'

export function InquiryCreatePage() {
  const navigate = useNavigate()
  const [inquiryType, setInquiryType] = useState('')
  const [inquiryContent, setInquiryContent] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    if (!inquiryType) {
      setError('문의 유형을 선택해주세요.')
      return
    }

    setLoading(true)
    try {
      await api.post('/inquiries', { inquiryType, inquiryContent })
      navigate('/mypage')
    } catch (err) {
      setError(err.response?.data?.message ?? '등록에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-lg">
      <Card>
        <CardHeader>
          <CardTitle>문의 / 신고 등록</CardTitle>
          <CardDescription>
            사기, 노쇼, 포인트 관련 문제 등을 등록해주세요.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="inquiryType">유형</Label>
              <Select value={inquiryType} onValueChange={setInquiryType}>
                <SelectTrigger id="inquiryType" className="w-full">
                  <SelectValue placeholder="유형을 선택하세요" />
                </SelectTrigger>
                <SelectContent>
                  {Object.entries(INQUIRY_TYPE_LABELS).map(([value, label]) => (
                    <SelectItem key={value} value={value}>
                      {label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="inquiryContent">내용</Label>
              <Textarea
                id="inquiryContent"
                rows={6}
                value={inquiryContent}
                onChange={(e) => setInquiryContent(e.target.value)}
                required
              />
            </div>
            {error && <p className="text-sm text-destructive">{error}</p>}
            <Button type="submit" disabled={loading} className="w-full">
              {loading ? '등록 중...' : '등록하기'}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
