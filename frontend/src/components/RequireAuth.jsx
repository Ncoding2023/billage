import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '@/lib/auth'

export function RequireAuth({ children }) {
  const { member } = useAuth()
  const location = useLocation()

  if (!member) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  return children
}
