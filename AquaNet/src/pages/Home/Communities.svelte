<!-- Svelte 4.2.11 -->

<script lang="ts">
  import { t } from "../../libs/i18n";
  import CommunityCard from "../../components/CommunityCard.svelte";
  import { DISCORD_INVITE, QQ_INVITE, TELEGRAM_INVITE } from "../../libs/config";
  import DashboardTabs from "./DashboardTabs.svelte";
</script>

<main class="content">
  <DashboardTabs />
  <div class="setup-instructions">
    <h2>{t('home.join-community')}</h2>
    <div class="grid cols-3 gap-4">
      {#if DISCORD_INVITE}
        <CommunityCard color="82, 93, 233" icon="ic:baseline-discord" href={DISCORD_INVITE}>
          <h3>{t('home.community.discord')}</h3>
        </CommunityCard>
      {/if}

      {#if TELEGRAM_INVITE}
        <CommunityCard color="46, 163, 224" icon="mingcute:telegram-fill" href={TELEGRAM_INVITE}>
          <h3>{t('home.community.telegram')}</h3>
        </CommunityCard>
      {/if}

      {#if QQ_INVITE}
        <CommunityCard color="226, 60, 68" icon="ri:qq-fill" href={QQ_INVITE}>
          <h3>{t('home.community.qq')}</h3>
        </CommunityCard>
      {/if}
    </div>
  </div>
</main>

<style lang="sass">

</style>
