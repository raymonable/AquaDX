<!-- Svelte 4.2.11 -->

<script lang="ts">
  import { t } from "../../libs/i18n";
  import CommunityCard from "../../components/CommunityCard.svelte";
  import { DISCORD_INVITE, GITHUB_REPOSITORY, QQ_INVITE, TELEGRAM_INVITE } from "../../libs/config";
  import DashboardTabs from "../../components/DashboardTabs.svelte";
</script>

<main class="content">
  <DashboardTabs />
  <div class="setup-instructions">
    <h2>{t('home.join-community')}</h2>
    <div class="communities grid cols-2 gap-4">
      {#if DISCORD_INVITE}
        <CommunityCard color="82, 93, 233" icon="ic:baseline-discord" href={DISCORD_INVITE}>
          <span>{t('home.community.discord')}</span>
        </CommunityCard>
      {/if}

      {#if TELEGRAM_INVITE}
        <CommunityCard color="46, 163, 224" icon="mingcute:telegram-fill" href={TELEGRAM_INVITE}>
          <span>{t('home.community.telegram')}</span>
        </CommunityCard>
      {/if}

      {#if QQ_INVITE}
        <CommunityCard color="226, 60, 68" icon="ri:qq-fill" href={QQ_INVITE}>
          <span>{t('home.community.qq')}</span>
        </CommunityCard>
      {/if}

      {#if GITHUB_REPOSITORY}
        <CommunityCard color="245, 245, 249" icon="ri:github-fill" href={GITHUB_REPOSITORY}>
          <span>{t('home.community.github')}</span>
        </CommunityCard>
      {/if}
    </div>
  </div>
</main>

<style lang="sass">
  .communities 
    margin: 0 1rem
</style>
