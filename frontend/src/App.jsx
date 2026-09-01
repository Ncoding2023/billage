import { Route, Routes } from 'react-router-dom'
import { Layout } from '@/components/Layout'
import { RequireAdmin } from '@/components/RequireAdmin'
import { RequireAuth } from '@/components/RequireAuth'
import { RequireUser } from '@/components/RequireUser'
import { AdminPage } from '@/pages/AdminPage'
import { InquiryCreatePage } from '@/pages/InquiryCreatePage'
import { ItemCreatePage } from '@/pages/ItemCreatePage'
import { ItemDetailPage } from '@/pages/ItemDetailPage'
import { ItemEditPage } from '@/pages/ItemEditPage'
import { ItemListPage } from '@/pages/ItemListPage'
import { LoginPage } from '@/pages/LoginPage'
import { MyPage } from '@/pages/MyPage'
import { RentalDetailPage } from '@/pages/RentalDetailPage'
import { RentalRequestPage } from '@/pages/RentalRequestPage'
import { SignupPage } from '@/pages/SignupPage'

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<ItemListPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/items/:itemNo" element={<ItemDetailPage />} />
        <Route
          path="/items/new"
          element={
            <RequireAuth>
              <ItemCreatePage />
            </RequireAuth>
          }
        />
        <Route
          path="/items/:itemNo/edit"
          element={
            <RequireAuth>
              <ItemEditPage />
            </RequireAuth>
          }
        />
        <Route
          path="/items/:itemNo/rent"
          element={
            <RequireAuth>
              <RentalRequestPage />
            </RequireAuth>
          }
        />
        <Route
          path="/rentals/:rentalNo"
          element={
            <RequireAuth>
              <RentalDetailPage />
            </RequireAuth>
          }
        />
        <Route
          path="/mypage"
          element={
            <RequireAuth>
              <MyPage />
            </RequireAuth>
          }
        />
        <Route
          path="/inquiries/new"
          element={
            <RequireUser>
              <InquiryCreatePage />
            </RequireUser>
          }
        />
        <Route
          path="/admin"
          element={
            <RequireAdmin>
              <AdminPage />
            </RequireAdmin>
          }
        />
      </Route>
    </Routes>
  )
}

export default App