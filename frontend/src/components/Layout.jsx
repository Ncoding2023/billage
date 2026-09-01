import { Link, Outlet, useNavigate } from 'react-router-dom'
import { Button, buttonVariants } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import { useAuth } from '@/lib/auth'

export function Layout() {
  const { member, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <div className="min-h-screen flex flex-col">
      <header className="border-b">
        <div className="mx-auto max-w-5xl flex items-center justify-between px-4 py-3">
          <Link to="/" className="text-lg font-semibold">
            Billage
          </Link>
          <nav className="flex items-center gap-3">
            {member ? (
              <>
                {member.role === 'ADMIN' && (
                  <Link
                    to="/admin"
                    className={cn(buttonVariants({ variant: 'ghost', size: 'sm' }))}
                  >
                    관리자
                  </Link>
                )}

                {member.role !== 'ADMIN' && (
                  <Link
                    to="/inquiries/new"
                    className={cn(buttonVariants({ variant: 'ghost', size: 'sm' }))}
                  >
                    문의하기
                  </Link>
                )}

                <Link
                  to="/mypage"
                  className={cn(buttonVariants({ variant: 'ghost', size: 'sm' }))}
                >
                  {member.nickname}님
                </Link>
                <Link
                  to="/items/new"
                  className={cn(buttonVariants({ variant: 'outline', size: 'sm' }))}
                >
                  물품 등록
                </Link>
                <Button variant="outline" size="sm" onClick={handleLogout}>
                  로그아웃
                </Button>
              </>
            ) : (
              <>
                <Link
                  to="/login"
                  className={cn(buttonVariants({ variant: 'ghost', size: 'sm' }))}
                >
                  로그인
                </Link>
                <Link to="/signup" className={cn(buttonVariants({ size: 'sm' }))}>
                  회원가입
                </Link>
              </>
            )}
          </nav>
        </div>
      </header>
      <main className="flex-1 mx-auto w-full max-w-5xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}