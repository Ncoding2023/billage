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
import { useAuth } from '@/lib/auth'

const initialForm = {
  email: '',
  password: '',
  name: '',
  nickname: '',
  phone: '',
}

export function SignupPage() {
  const { signUp } = useAuth()
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
    setLoading(true)
    try {
      await signUp(form)
      navigate('/login')
    } catch (err) {
      setError(err.response?.data?.message ?? '회원가입에 실패했습니다.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-sm">
      <Card>
        <CardHeader>
          <CardTitle>회원가입</CardTitle>
          <CardDescription>
            가입 즉시 5,000포인트가 지급됩니다.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="email">이메일</Label>
              <Input
                id="email"
                type="email"
                value={form.email}
                onChange={handleChange('email')}
                required
              />
              <p className="text-xs text-muted-foreground">
                이메일(example@example.com) 형식에 맞게 입력해주세요.
              </p>
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                type="password"
                minLength={8}
                value={form.password}
                onChange={handleChange('password')}
                required
              />
              <p className="text-xs text-muted-foreground">
                8자 이상, 특수문자(!@#$%^&* 등)를 포함해야 합니다.
              </p>
            </div>
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
              <Label htmlFor="nickname">닉네임</Label>
              <Input
                id="nickname"
                value={form.nickname}
                onChange={handleChange('nickname')}
                required
              />
            </div>
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="phone">전화번호</Label>
              <Input
                id="phone"
                placeholder="010-0000-0000"
                value={form.phone}
                onChange={handleChange('phone')}
                required
              />
              <p className="text-xs text-muted-foreground">
                전화번호(010-0000-0000) 형식에 맞게 입력해주세요.
              </p>
            </div>
            {error && <p className="text-sm text-destructive">{error}</p>}
            <Button type="submit" disabled={loading} className="w-full">
              {loading ? '가입 중...' : '회원가입'}
            </Button>
          </form>
          <p className="mt-4 text-center text-sm text-muted-foreground">
            이미 계정이 있으신가요?{' '}
            <Link to="/login" className="text-primary underline underline-offset-4">
              로그인
            </Link>
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
