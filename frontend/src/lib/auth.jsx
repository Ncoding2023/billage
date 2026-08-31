import { createContext, useContext, useState } from 'react'
import { api } from '@/lib/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [member, setMember] = useState(() => {
    const stored = localStorage.getItem('member')
    return stored ? JSON.parse(stored) : null
  })

  async function login(email, password) {
    const { data } = await api.post('/auth/login', { email, password })
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('member', JSON.stringify(data.member))
    setMember(data.member)
    return data.member
  }

  async function signUp(payload) {
    const { data } = await api.post('/members/signup', payload)
    return data
  }

  function logout() {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('member')
    setMember(null)
  }

  function updateMemberInfo(updates) {
    setMember((prev) => {
      const next = { ...prev, ...updates }
      localStorage.setItem('member', JSON.stringify(next))
      return next
    })
  }

  return (
    <AuthContext.Provider value={{ member, login, signUp, logout, updateMemberInfo }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
