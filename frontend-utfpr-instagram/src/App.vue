<script setup>
import { RouterView } from 'vue-router'
import { ref } from 'vue'

const showDebug = ref(false)
const useFakeToken = ref(sessionStorage.getItem('use_fake_token') === 'true')
const fakeToken = ref(sessionStorage.getItem('fake_token') || '')
const realToken = ref('')

const toggleDebug = () => {
  showDebug.value = !showDebug.value
  if (showDebug.value) {
    realToken.value = sessionStorage.getItem('instagram_token') || 'Nenhum token real (Não logado)'
    fakeToken.value = sessionStorage.getItem('fake_token') || ''
    useFakeToken.value = sessionStorage.getItem('use_fake_token') === 'true'
  }
}

const saveDebug = () => {
  sessionStorage.setItem('use_fake_token', useFakeToken.value)
  sessionStorage.setItem('fake_token', fakeToken.value)
}
</script>

<template>
  <RouterView />
  
  <!-- Floating Debug Button -->
  <button class="debug-fab" @click="toggleDebug" title="Debug Panel">🐛</button>

  <!-- Debug Panel -->
  <div v-if="showDebug" class="debug-panel">
    <div class="debug-header">
      <h3>Painel de Teste (Token)</h3>
      <button @click="toggleDebug" class="btn-close">&times;</button>
    </div>
    
    <div class="debug-body">
      <div class="form-group">
        <label>Token Real (Ativo/Original):</label>
        <textarea readonly :value="realToken" rows="3"></textarea>
      </div>
      
      <div class="form-group checkbox-group">
        <input type="checkbox" id="useFake" v-model="useFakeToken" @change="saveDebug" />
        <label for="useFake">Inativar token original e usar modificado</label>
      </div>

      <div class="form-group" v-if="useFakeToken">
        <label>Token Modificado (Falso/Expirado):</label>
        <textarea 
          v-model="fakeToken" 
          @input="saveDebug" 
          rows="3" 
          placeholder="Cole aqui o token adulterado..."
        ></textarea>
      </div>
      
      <div class="info-text">
        <small>As requisições interceptarão as alterações na mesma hora sem recarregar a página!</small>
      </div>
    </div>
  </div>
</template>

<style scoped>
.debug-fab {
  position: fixed;
  bottom: 20px;
  left: 20px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #333;
  color: #fff;
  border: 2px solid #555;
  font-size: 1.5rem;
  cursor: pointer;
  z-index: 9999;
  box-shadow: 0 4px 10px rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s;
}

.debug-fab:hover {
  transform: scale(1.1);
  background-color: #444;
}

.debug-panel {
  position: fixed;
  bottom: 80px;
  left: 20px;
  width: 380px;
  background-color: #1e1e1e;
  border: 1px solid #444;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.8);
  z-index: 9999;
  color: #eee;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.debug-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background-color: #2a2a2a;
  border-bottom: 1px solid #444;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.debug-header h3 {
  margin: 0;
  font-size: 1rem;
  color: #4ade80;
}

.btn-close {
  background: none;
  border: none;
  color: #aaa;
  font-size: 1.5rem;
  cursor: pointer;
  line-height: 1;
}
.btn-close:hover { color: #fff; }

.debug-body {
  padding: 15px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-group label {
  font-size: 0.85rem;
  color: #bbb;
}

.form-group textarea {
  width: 100%;
  background-color: #111;
  border: 1px solid #555;
  color: #0f0;
  padding: 8px;
  border-radius: 4px;
  resize: vertical;
  font-family: monospace;
  font-size: 0.8rem;
  outline: none;
}
.form-group textarea:focus { border-color: #4ade80; }

.checkbox-group {
  flex-direction: row;
  align-items: center;
  gap: 10px;
  background: #2a2a2a;
  padding: 8px;
  border-radius: 4px;
}

.checkbox-group input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.checkbox-group label {
  color: #fff;
  cursor: pointer;
  font-size: 0.9rem;
  user-select: none;
}

.info-text {
  color: #a8a8a8;
  text-align: center;
  margin-top: -5px;
}
</style>
