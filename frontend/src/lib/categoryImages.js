import camping from '@/assets/dumy/camping.webp'
import clothes from '@/assets/dumy/clothes.webp'
import living from '@/assets/dumy/living.webp'
import tool from '@/assets/dumy/tool.webp'
import { API_ORIGIN } from '@/lib/api'

export const CATEGORY_DUMMY_IMAGES = {
  TOOL: tool,
  CLOTHES: clothes,
  CAMPING: camping,
  LIVING: living,
}

export function getCategoryDummyImage(category) {
  return CATEGORY_DUMMY_IMAGES[category] ?? tool
}

/** 등록 이미지가 있으면 서버 URL, 없으면 카테고리 더미 이미지 */
export function getItemImageSrc(category, imagePath) {
  if (imagePath) {
    return `${API_ORIGIN}${imagePath}`
  }
  return getCategoryDummyImage(category)
}
