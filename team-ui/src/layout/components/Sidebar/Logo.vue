<template>
  <div class="sidebar-logo-container" :class="{ 'collapse': collapse }" :style="{ backgroundColor: sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <div class="sidebar-logo-wrapper">
          <img v-if="logo" :src="logo" class="sidebar-logo" />
          <h1 v-else class="sidebar-title">{{ title }}</h1>
        </div>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <div class="sidebar-logo-wrapper">
          <img v-if="logo" :src="logo" class="sidebar-logo" />
          <h1 class="sidebar-title">{{ title }}</h1>
        </div>
      </router-link>
    </transition>
  </div>
</template>

<script setup>
import variables from '@/assets/styles/variables.module.scss'
import logo from '@/assets/logo/logo.png'
import useSettingsStore from '@/store/modules/settings'

defineProps({
  collapse: {
    type: Boolean,
    required: true
  }
})

const title = import.meta.env.VITE_APP_TITLE;
const settingsStore = useSettingsStore();
const sideTheme = computed(() => settingsStore.sideTheme);
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  background: #2b2f3a;
  text-align: center;
  overflow: hidden;

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
  }

  & .sidebar-logo-wrapper {
    display: flex;
  }

  & .sidebar-logo {
    width: 32px;
    height: 32px;
    margin-right: 10px; // Adjust margin as needed
    margin-top: 14px;
    margin-left: 12px;
  }

  & .sidebar-title {
    color: #fff;
    font-weight: 600;
    font-size: 14px;
    font-family: Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
    margin-top: 23px;
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
  }
}
</style>