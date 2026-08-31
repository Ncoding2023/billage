import { useEffect, useState } from 'react'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { api } from '@/lib/api'

export function AdminMembersTab() {
  const [members, setMembers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  function load() {
    setLoading(true)
    api
      .get('/admin/members')
      .then((res) => setMembers(res.data))
      .catch((err) => setError(err.response?.data?.message ?? '회원 목록을 불러오지 못했습니다.'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  async function handleToggle(member) {
    const action = member.enabled ? 'suspend' : 'activate'
    if (member.enabled && !window.confirm(`${member.nickname}님 계정을 정지하시겠습니까?`)) {
      return
    }
    try {
      await api.patch(`/admin/members/${member.memberNo}/${action}`)
      load()
    } catch (err) {
      setError(err.response?.data?.message ?? '처리에 실패했습니다.')
    }
  }

  if (loading) {
    return <p className="text-muted-foreground">불러오는 중...</p>
  }

  if (error) {
    return <p className="text-destructive">{error}</p>
  }

  return (
    <div className="flex flex-col gap-2">
      {members.map((member) => (
        <Card key={member.memberNo}>
          <CardContent className="flex items-center justify-between text-sm">
            <div>
              <p className="font-medium">
                {member.nickname} · {member.name}
              </p>
              <p className="text-muted-foreground">
                {member.email} · {member.phone}
              </p>
            </div>
            <div className="flex items-center gap-2">
              <Badge variant="secondary">{member.role}</Badge>
              <Badge variant={member.enabled ? 'secondary' : 'destructive'}>
                {member.enabled ? '정상' : '정지됨'}
              </Badge>
              <Button
                size="sm"
                variant={member.enabled ? 'destructive' : 'outline'}
                onClick={() => handleToggle(member)}
              >
                {member.enabled ? '정지' : '활성화'}
              </Button>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
