<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { usuarioService } from '@/services/usuarioService'

const router = useRouter()

// Reactive Form State
const form = ref({
  nome: '',
  usuario: '',
  email: '',
  senha: '',
  biografia: ''
})

const isLoading = ref(false)
const errorMessage = ref(null)
const successMessage = ref(null)

const validateInternal = () => {
  // Padronizar usuário (lowercase, sem espaços)
  form.value.usuario = form.value.usuario.toLowerCase().replace(/\s+/g, '')
}

const handleCadastro = async () => {
  isLoading.value = true
  errorMessage.value = null
  successMessage.value = null
  
  try {
    const dataToSend = { ...form.value }
    // Remover campos opcionais que estejam vazios se preciso
    if (!dataToSend.biografia) delete dataToSend.biografia

    const resp = await usuarioService.cadastrar(dataToSend)
    
    // O Fetch retorna resp.ok como true se for status da família 200 (Inclusive 201 Created)
    if (resp.ok) {
      successMessage.value = 'Cadastro concluído! Redirecionando para as boas vindas...'
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      // Diferente do Axios, o Fetch não lança erro sozinho em status de rede 400.
      // Nós capturamos o payload do servidor aqui mesmo para mostrar o erro bonitinho
      const errorData = await resp.json().catch(() => null)
      if (errorData && errorData.mensagem) {
        errorMessage.value = errorData.mensagem
      } else {
        errorMessage.value = 'Ocorreu um erro ao comunicar com os servidores.'
      }
    }

  } catch (error) {
    // Um erro capturado aqui geralmente é o backend totalmente offline (Erro de Conexão CORTADA)
    errorMessage.value = 'Falha de rede. O servidor parece estar offline.'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <main class="page-wrapper">
    <div class="auth-card">
      <div class="brand-header">
        <h1>Instagram</h1>
        <p>Cadastre-se para ver fotos e vídeos dos seus amigos.</p>
      </div>

      <form @submit.prevent="handleCadastro" class="auth-form">
        <transition name="fade">
          <div v-if="errorMessage" class="alert-box error">{{ errorMessage }}</div>
        </transition>
        <transition name="fade">
          <div v-if="successMessage" class="alert-box success">{{ successMessage }}</div>
        </transition>

        <div class="input-group">
          <input 
            v-model="form.email" 
            type="email" 
            placeholder="E-mail principal" required 
            maxlength="35"
          />
        </div>

        <div class="input-group">
          <input 
            v-model="form.nome" 
            type="text" 
            placeholder="Nome Completo" required 
            minlength="3" maxlength="60"
          />
        </div>

        <div class="input-group">
          <input 
            v-model="form.usuario" 
            @input="validateInternal"
            type="text" 
            placeholder="Nome de Usuário (@)" required 
            minlength="3" maxlength="30"
          />
        </div>

        <div class="input-group">
          <input 
            v-model="form.senha" 
            type="password" 
            placeholder="Escolha uma senha" required 
            minlength="8" maxlength="24"
          />
        </div>

        <div class="input-group">
          <textarea 
            v-model="form.biografia" 
            placeholder="Uma breve biografia (Opcional)" 
            maxlength="150"
            rows="2"
          ></textarea>
        </div>

        <button type="submit" class="btn-primary" :disabled="isLoading">
          <span v-if="isLoading" class="spinner"></span>
          <span v-else>Cadastre-se</span>
        </button>
      </form>

      <div class="auth-footer">
        <p>Tem uma conta? <RouterLink to="/login">Conecte-se</RouterLink></p>
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
}

.brand-header {
  text-align: center;
  margin-bottom: 2rem;
}

.brand-header h1 {
  font-family: 'Times New Roman', serif; /* Font aesthetic like Instas logo */
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

.input-group input,
.input-group textarea {
  width: 100%;
  background-color: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-base);
  color: var(--text-primary);
  padding: 0.75rem 1rem;
  outline: none;
  transition: all 0.3s ease;
}

.input-group input:focus,
.input-group textarea:focus {
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

.alert-box.success {
  background-color: rgba(82, 196, 26, 0.1);
  border: 1px solid var(--color-success);
  color: var(--color-success);
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
