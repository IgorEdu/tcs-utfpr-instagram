<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { usuarioService } from '@/services/usuarioService'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  usuario: '',
  senha: ''
})

const isLoading = ref(false)
const errorMessage = ref(null)

const handleLogin = async () => {
  isLoading.value = true
  errorMessage.value = null
  
  try {
    const resp = await usuarioService.login(form.value)
    
    if (resp.ok) {
      const data = await resp.json()
      // Guardar o token e os dados do usuário usando a store Pinia
      if (data && data.dados && data.dados.token) {
        authStore.setAuth(data.dados.token, data.dados.usuario)
      }
      
      // Redireciona para a tela inicial
      router.push('/home')
    } else {
      const errorData = await resp.json().catch(() => null)
      if (errorData && errorData.mensagem) {
        errorMessage.value = errorData.mensagem
      } else {
        errorMessage.value = 'Credenciais inválidas. Tente novamente.'
      }
    }
  } catch (error) {
    errorMessage.value = 'Falha de rede. O servidor parece estar offline.'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <main class="page-wrapper">
    <div class="auth-card">
      <RouterLink to="/configuracoes" class="settings-icon" title="Configurações">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"></path>
          <circle cx="12" cy="12" r="3"></circle>
        </svg>
      </RouterLink>

      <div class="brand-header">
        <h1>Instagram</h1>
        <p>Acesse sua conta para continuar.</p>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <transition name="fade">
          <div v-if="errorMessage" class="alert-box error">{{ errorMessage }}</div>
        </transition>

        <div class="input-group">
          <input 
            v-model="form.usuario" 
            type="text" 
            placeholder="Nome de Usuário" required 
          />
        </div>

        <div class="input-group">
          <input 
            v-model="form.senha" 
            type="password" 
            placeholder="Senha" required 
          />
        </div>

        <button type="submit" class="btn-primary" :disabled="isLoading">
          <span v-if="isLoading" class="spinner"></span>
          <span v-else>Entrar</span>
        </button>
      </form>

      <div class="auth-footer">
        <p>Não tem uma conta? <RouterLink to="/cadastro">Cadastre-se</RouterLink></p>
      </div>
    </div>
  </main>
</template>

<style scoped>
.page-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 1rem;
}

.auth-card {
  width: 100%;
  max-width: 380px;
  background-color: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
  position: relative;
}

.settings-icon {
  position: absolute;
  top: 1rem;
  right: 1rem;
  color: var(--text-secondary);
  transition: color 0.3s ease, transform 0.3s ease;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.settings-icon:hover {
  color: var(--color-brand);
  transform: rotate(90deg);
}

.brand-header {
  text-align: center;
  margin-bottom: 2rem;
}

.brand-header h1 {
  font-family: 'Times New Roman', serif;
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  letter-spacing: 1px;
}

.brand-header p {
  color: var(--text-secondary);
  font-size: 0.95rem;
  line-height: 1.4;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.input-group input {
  width: 100%;
  background-color: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  color: var(--text-primary);
  padding: 0.75rem 1rem;
  outline: none;
  transition: all 0.3s ease;
}

.input-group input:focus {
  border-color: var(--color-brand);
  background-color: var(--bg-tertiary);
}

.btn-primary {
  background-color: var(--color-brand);
  color: #fff;
  border: none;
  padding: 0.75rem;
  border-radius: var(--border-radius-base);
  cursor: pointer;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
  min-height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-primary:hover:not(:disabled) {
  background-color: var(--color-brand-hover);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.auth-footer {
  margin-top: 1.5rem;
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-color);
  font-size: 0.9rem;
}

/* Base Alerts */
.alert-box {
  padding: 0.75rem;
  border-radius: var(--border-radius-base);
  font-size: 0.85rem;
  text-align: center;
}

.alert-box.error {
  background-color: var(--color-error-bg);
  border: 1px solid var(--color-error);
  color: var(--color-error);
}

/* Spinner Loader CSS */
.spinner {
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
