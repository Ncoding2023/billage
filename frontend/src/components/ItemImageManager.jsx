import { useState } from 'react'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { API_ORIGIN, api } from '@/lib/api'

export function ItemImageManager({ itemNo, images, onChanged }) {
  const [file, setFile] = useState(null)
  const [asMain, setAsMain] = useState(images.length === 0)
  const [uploading, setUploading] = useState(false)
  const [error, setError] = useState('')

  async function handleUpload(e) {
    e.preventDefault()
    if (!file) {
      return
    }
    setError('')
    setUploading(true)
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('mainImage', asMain)
      await api.post(`/items/${itemNo}/images`, formData)
      setFile(null)
      onChanged()
    } catch (err) {
      setError(err.response?.data?.message ?? '이미지 업로드에 실패했습니다.')
    } finally {
      setUploading(false)
    }
  }

  async function handleSetMain(imageNo) {
    try {
      await api.patch(`/items/${itemNo}/images/${imageNo}/main`)
      onChanged()
    } catch (err) {
      setError(err.response?.data?.message ?? '대표이미지 지정에 실패했습니다.')
    }
  }

  async function handleDeleteImage(imageNo) {
    if (!window.confirm('이 이미지를 삭제하시겠습니까?')) {
      return
    }
    try {
      await api.delete(`/items/${itemNo}/images/${imageNo}`)
      onChanged()
    } catch (err) {
      setError(err.response?.data?.message ?? '이미지 삭제에 실패했습니다.')
    }
  }

  return (
    <div className="border-t pt-4">
      <p className="mb-3 text-sm font-medium">이미지 관리</p>

      {images.length > 0 && (
        <div className="mb-3 grid grid-cols-3 gap-2 sm:grid-cols-4">
          {images.map((image) => (
            <div key={image.imageNo} className="relative">
              <img
                src={`${API_ORIGIN}${image.imagePath}`}
                alt={image.originalFileName}
                className="aspect-square w-full rounded-lg object-cover"
              />
              {image.mainImage && (
                <Badge className="absolute left-1 top-1" variant="secondary">
                  대표
                </Badge>
              )}
              <div className="mt-1 flex gap-1">
                {!image.mainImage && (
                  <Button
                    type="button"
                    size="xs"
                    variant="outline"
                    onClick={() => handleSetMain(image.imageNo)}
                  >
                    대표지정
                  </Button>
                )}
                <Button
                  type="button"
                  size="xs"
                  variant="destructive"
                  onClick={() => handleDeleteImage(image.imageNo)}
                >
                  삭제
                </Button>
              </div>
            </div>
          ))}
        </div>
      )}

      <form onSubmit={handleUpload} className="flex flex-col gap-2 sm:flex-row sm:items-center">
        <Input
          type="file"
          accept="image/*"
          onChange={(e) => setFile(e.target.files?.[0] ?? null)}
          className="sm:flex-1"
        />
        <label className="flex items-center gap-1.5 text-sm whitespace-nowrap">
          <input
            type="checkbox"
            checked={asMain}
            onChange={(e) => setAsMain(e.target.checked)}
          />
          대표이미지로 설정
        </label>
        <Button type="submit" size="sm" disabled={!file || uploading}>
          {uploading ? '업로드 중...' : '이미지 추가'}
        </Button>
      </form>
      {error && <p className="mt-2 text-sm text-destructive">{error}</p>}
    </div>
  )
}
