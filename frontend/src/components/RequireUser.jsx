import { Navigate } from 'react-router-dom'
import { useAuth } from '@/lib/auth'

export function RequireUser({ children }) {
  const { member } = useAuth()

  if (!member) {
    return <Navigate to="/login" replace />
  }

  if (member.role !== 'USER') {
    return <Navigate to="/" replace />
  }

  return children
}