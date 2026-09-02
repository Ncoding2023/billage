import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
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

const initialForm = {
  name: '',
  email: '',
  newPassword: '',
  confirmPassword: '',
}

export function ResetPasswordPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState(initialForm)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  function handleChange(field) {
    return (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    if (form.newPassword !== form.confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.')
      return
    }

    setLoading(true)
    try {
      await api.post('/auth/reset-password', form)
      navigate('/login', { replace: true })
    } catch (err) {
      setError(err.response?.data?.message ?? '비밀번호 변경에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-sm">
      <Card>
        <CardHeader>
          <CardTitle>비밀번호 찾기</CardTitle>
          <CardDescription>
            가입 시 이름과 이메일을 확인한 뒤 새 비밀번호로 변경합니다.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="name">이름</Label>
              <Input
                id="name"
                value={form.name}
                onChange={handleChange('name')}
                required
              />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="email">이메일</Label>
              <Input
                id="email"
                type="email"
                value={form.email}
                onChange={handleChange('email')}
                required
              />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="newPassword">변경할 비밀번호</Label>
              <Input
                id="newPassword"
                type="password"
                minLength={8}
                value={form.newPassword}
                onChange={handleChange('newPassword')}
                required
              />
              <p className="text-xs text-muted-foreground">
                8자 이상, 특수문자(!@#$%^&* 등)를 포함해야 합니다.
              </p>
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="confirmPassword">변경할 비밀번호 확인</Label>
              <Input
                id="confirmPassword"
                type="password"
                minLength={8}
                value={form.confirmPassword}
                onChange={handleChange('confirmPassword')}
                required
              />
            </div>
            {error && <p className="text-sm text-destructive">{error}</p>}
            <Button type="submit" disabled={loading} className="w-full">
              {loading ? '변경 중...' : '비밀번호 변경'}
            </Button>
          </form>
          <p className="mt-4 text-center text-sm text-muted-foreground">
            <Link to="/login" className="text-primary underline underline-offset-4">
              로그인으로 돌아가기
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
