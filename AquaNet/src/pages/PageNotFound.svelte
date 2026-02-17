<script>
  import StatusOverlays from "../components/StatusOverlays.svelte";
  import { t } from "../libs/i18n";
</script>
<StatusOverlays error={t("404", {pathname: new URL(location.href).pathname})} />