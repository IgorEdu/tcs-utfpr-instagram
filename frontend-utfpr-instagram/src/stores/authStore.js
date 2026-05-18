import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  // Inicializa com o que estiver no sessionStorage, permitindo que a sessão sobreviva a reloads da página
  const token = ref(sessionStorage.getItem('instagram_token') || null)
  const user = ref(JSON.parse(sessionStorage.getItem('instagram_user') || 'null'))

  const setAuth = (newToken, newUser) => {
    token.value = newToken
    user.value = newUser
    
    // Persiste os dados na sessão
    sessionStorage.setItem('instagram_token', newToken)
    sessionStorage.setItem('instagram_user', JSON.stringify(newUser))
  }

  const logout = () => {
    token.value = null
    user.value = null
    sessionStorage.removeItem('instagram_token')
    sessionStorage.removeItem('instagram_user')
  }

  return { token, user, setAuth, logout }
})
