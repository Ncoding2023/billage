import axios from 'axios'

export const API_ORIGIN = 'http://localhost:8080'

export const api = axios.create({
  baseURL: `${API_ORIGIN}/api`,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})
