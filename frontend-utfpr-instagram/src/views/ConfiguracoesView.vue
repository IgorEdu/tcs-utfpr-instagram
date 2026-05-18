<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const ip = ref('')
const porta = ref('')
const saved = ref(false)

onMounted(() => {
  ip.value = localStorage.getItem('api_ip') || 'localhost'
  porta.value = localStorage.getItem('api_port') || '8080'
})

const saveConfig = () => {
  localStorage.setItem('api_ip', ip.value)
  localStorage.setItem('api_port', porta.value)
  
  saved.value = true
  setTimeout(() => {
    saved.value = false
    goBack()
  }, 1500)
}

const goBack = () => {
  if (window.history.length > 2) {
    router.back()
  } else {
    router.push('/login')
  }
}
</script>

<template>
  <main class="page-wrapper">
    <div class="auth-card">
      <div class="brand-header">
        <h1>Configurações</h1>
        <p>Ajuste as configurações de conexão com o servidor.</p>
      </div>

      <form @submit.prevent="saveConfig" class="auth-form">
        <transition name="fade">
          <div v-if="saved" class="alert-box success">Configurações salvas com sucesso!</div>
        </transition>

        <div class="input-group">
          <label for="ip">Endereço IP</label>
          <input 
            id="ip"
            v-model="ip" 
            type="text" 
            placeholder="Ex: 192.168.1.100 ou localhost" required 
          />
        </div>

        <div class="input-group">
          <label for="porta">Porta</label>
          <input 
            id="porta"
            v-model="porta" 
            type="text" 
            placeholder="Ex: 8080" required 
          />
        </div>

        <div class="button-group">
          <button type="button" class="btn-secondary" @click="goBack">Voltar</button>
          <button type="submit" class="btn-primary">Salvar</button>
        </div>
      </form>
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
  font-family: 'Times New Roman', serif;
  font-size: 2rem;
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
  gap: 1.25rem;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.input-group label {
  font-size: 0.9rem;
  color: var(--text-secondary);
  font-weight: 500;
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

.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 0.5rem;
}

.btn-primary, .btn-secondary {
  flex: 1;
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

.btn-primary {
  background-color: var(--color-brand);
  color: #fff;
  border: none;
}

.btn-primary:hover {
  background-color: var(--color-brand-hover);
}

.btn-secondary {
  background-color: transparent;
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover {
  background-color: var(--bg-tertiary);
}

/* Base Alerts */
.alert-box {
  padding: 0.75rem;
  border-radius: var(--border-radius-base);
  font-size: 0.85rem;
  text-align: center;
}

.alert-box.success {
  background-color: rgba(46, 204, 113, 0.1);
  border: 1px solid #2ecc71;
  color: #2ecc71;
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
