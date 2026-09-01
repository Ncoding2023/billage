import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Badge } from '@/components/ui/badge'
import { Button, buttonVariants } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { api } from '@/lib/api'
import { useAuth } from '@/lib/auth'
import { cn } from '@/lib/utils'
import {
  CATEGORY_LABELS,
  INQUIRY_STATUS_LABELS,
  INQUIRY_TYPE_LABELS,
  POINT_TYPE_LABELS,
  RENTAL_STATUS_LABELS,
} from '@/lib/constants'

export function MyPage() {
  const { member, updateMemberInfo } = useAuth()
  const [pointBalance, setPointBalance] = useState(0)
  const [items, setItems] = useState([])
  const [rentalsAsRenter, setRentalsAsRenter] = useState([])
  const [rentalsAsProvider, setRentalsAsProvider] = useState([])
  const [pointHistories, setPointHistories] = useState([])
  const [inquiries, setInquiries] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    setLoading(true)
    Promise.all([
      api.get(`/members/${member.memberNo}/points/balance`),
      api.get(`/members/${member.memberNo}/items`),
      api.get('/rentals?role=renter'),
      api.get('/rentals?role=provider'),
      api.get(`/members/${member.memberNo}/points/histories`),
      api.get('/inquiries/mine'),
    ])
      .then(([balanceRes, itemsRes, renterRes, providerRes, historiesRes, inquiriesRes]) => {
        setPointBalance(balanceRes.data.point)
        setItems(itemsRes.data)
        setRentalsAsRenter(renterRes.data)
        setRentalsAsProvider(providerRes.data)
        setPointHistories(historiesRes.data)
        setInquiries(inquiriesRes.data)
      })
      .catch((err) => setError(err.response?.data?.message ?? '정보를 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }, [member.memberNo])

  if (loading) {
    return <p className="text-muted-foreground">불러오는 중...</p>
  }

  if (error) {
    return <p className="text-destructive">{error}</p>
  }

  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-6">
      <ProfileCard pointBalance={pointBalance} onUpdated={updateMemberInfo} />

      <Tabs defaultValue="items">
        <TabsList>
          <TabsTrigger value="items">내 물품 ({items.length})</TabsTrigger>
          <TabsTrigger value="renting">신청한 대여 ({rentalsAsRenter.length})</TabsTrigger>
          <TabsTrigger value="providing">받은 대여요청 ({rentalsAsProvider.length})</TabsTrigger>
          <TabsTrigger value="points">포인트 내역</TabsTrigger>
          <TabsTrigger value="inquiries">문의내역 ({inquiries.length})</TabsTrigger>
        </TabsList>

        <TabsContent value="items" className="flex flex-col gap-2">
          {items.length === 0 && <EmptyRow text="등록한 물품이 없습니다." />}
          {items.map((item) => (
            <Link key={item.itemNo} to={`/items/${item.itemNo}`}>
              <Card className="transition-colors hover:bg-muted/50">
                <CardContent className="flex items-center justify-between text-sm">
                  <div>
                    <p className="font-medium">{item.itemName}</p>
                    <p className="text-muted-foreground">
                      {CATEGORY_LABELS[item.category] ?? item.category}
                    </p>
                  </div>
                  <span>{item.rentalPoint.toLocaleString()}P</span>
                </CardContent>
              </Card>
            </Link>
          ))}
        </TabsContent>

        <TabsContent value="renting" className="flex flex-col gap-2">
          {rentalsAsRenter.length === 0 && <EmptyRow text="신청한 대여가 없습니다." />}
          {rentalsAsRenter.map((rental) => (
            <RentalRow key={rental.rentalNo} rental={rental} />
          ))}
        </TabsContent>

        <TabsContent value="providing" className="flex flex-col gap-2">
          {rentalsAsProvider.length === 0 && <EmptyRow text="받은 대여요청이 없습니다." />}
          {rentalsAsProvider.map((rental) => (
            <RentalRow key={rental.rentalNo} rental={rental} showRenter />
          ))}
        </TabsContent>

        <TabsContent value="points" className="flex flex-col gap-2">
          {pointHistories.length === 0 && <EmptyRow text="포인트 내역이 없습니다." />}
          {pointHistories.map((history) => (
            <Card key={history.pointHistoryNo}>
              <CardContent className="flex items-center justify-between text-sm">
                <div>
                  <p className="font-medium">{history.pointContent}</p>
                  <p className="text-muted-foreground">
                    {POINT_TYPE_LABELS[history.pointType] ?? history.pointType}
                  </p>
                </div>
                <span
                  className={
                    history.pointAmount >= 0 ? 'text-primary' : 'text-destructive'
                  }
                >
                  {history.pointAmount >= 0 ? '+' : ''}
                  {history.pointAmount.toLocaleString()}P
                </span>
              </CardContent>
            </Card>
          ))}
        </TabsContent>

        <TabsContent value="inquiries" className="flex flex-col gap-2">
          {member.role !== 'ADMIN' && (
            <Link
              to="/inquiries/new"
              className={cn(buttonVariants({ variant: 'outline', size: 'sm' }), 'w-fit')}
            >
              새 문의 등록
            </Link>
          )}

          {inquiries.length === 0 && <EmptyRow text="등록한 문의가 없습니다." />}

          {inquiries.map((inquiry) => (
            <Card key={inquiry.inquiryNo}>
              <CardContent className="flex items-center justify-between text-sm">
                <div>
                  <p className="font-medium">
                    {INQUIRY_TYPE_LABELS[inquiry.inquiryType] ?? inquiry.inquiryType}
                  </p>
                  <p className="text-muted-foreground">{inquiry.inquiryContent}</p>
                </div>
                <Badge variant="secondary">
                  {INQUIRY_STATUS_LABELS[inquiry.processStatus] ?? inquiry.processStatus}
                </Badge>
              </CardContent>
            </Card>
          ))}
        </TabsContent>
      </Tabs>
    </div>
  )
}

function RentalRow({ rental, showRenter }) {
  return (
    <Link to={`/rentals/${rental.rentalNo}`}>
      <Card className="transition-colors hover:bg-muted/50">
        <CardContent className="flex items-center justify-between text-sm">
          <div>
            <p className="font-medium">{rental.itemName}</p>
            <p className="text-muted-foreground">
              {rental.rentalStartDate} ~ {rental.rentalEndDate}
              {showRenter && ` · ${rental.memberNickname}`}
            </p>
          </div>
          <Badge variant="secondary">
            {RENTAL_STATUS_LABELS[rental.rentalStatus] ?? rental.rentalStatus}
          </Badge>
        </CardContent>
      </Card>
    </Link>
  )
}

function EmptyRow({ text }) {
  return <p className="py-6 text-center text-sm text-muted-foreground">{text}</p>
}

function ProfileCard({ pointBalance, onUpdated }) {
  const { member } = useAuth()
  const [editing, setEditing] = useState(false)
  const [name, setName] = useState(member.name)
  const [nickname, setNickname] = useState(member.nickname)
  const [phone, setPhone] = useState(member.phone)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  async function handleSave(e) {
    e.preventDefault()
    setError('')
    setSaving(true)
    try {
      const { data } = await api.patch(`/members/${member.memberNo}`, {
        name,
        nickname,
        phone,
      })
      onUpdated(data)
      setEditing(false)
    } catch (err) {
      setError(err.response?.data?.message ?? '수정에 실패했습니다.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>내 정보</CardTitle>
        <span className="text-lg font-semibold">{pointBalance.toLocaleString()}P</span>
      </CardHeader>
      <CardContent>
        {editing ? (
          <form onSubmit={handleSave} className="flex flex-col gap-3">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="name">이름</Label>
              <Input
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="nickname">닉네임</Label>
              <Input
                id="nickname"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                required
              />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="phone">전화번호</Label>
              <Input
                id="phone"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                required
              />
            </div>
            {error && <p className="text-sm text-destructive">{error}</p>}
            <div className="flex gap-2">
              <Button type="submit" disabled={saving}>
                {saving ? '저장 중...' : '저장'}
              </Button>
              <Button type="button" variant="outline" onClick={() => setEditing(false)}>
                취소
              </Button>
            </div>
          </form>
        ) : (
          <div className="flex flex-col gap-1 text-sm">
            <Row label="이메일" value={member.email} />
            <Row label="이름" value={member.name} />
            <Row label="닉네임" value={member.nickname} />
            <Row label="전화번호" value={member.phone} />
            <Button
              variant="outline"
              size="sm"
              className="mt-2 w-fit"
              onClick={() => setEditing(true)}
            >
              정보 수정
            </Button>
          </div>
        )}
      </CardContent>
    </Card>
  )
}

function Row({ label, value }) {
  return (
    <div className="flex items-center justify-between border-b py-1.5 last:border-b-0">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium">{value}</span>
    </div>
  )
}