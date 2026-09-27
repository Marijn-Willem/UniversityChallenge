const seasonListLoader = new ElementLoader('selSeason', function () {
    return getOptionsUrl('season');
}, null);

const seasonUpsertLoader = new ElementLoader('inpId', function () {
    const id = document.getElementById('inpId').value;
    const nm = document.getElementById('nm').value;

    return `/season/upsert?nm=${nm}${!isEmptyOrNull(id) ? '&id=' + id: ''}`;
}, callBackUpsert)

function loadSeasonList() {
    seasonListLoader.loadElement();
}

function goToManageSeason() {
    const id = document.getElementById('selSeason').value;

    if (!isEmptyOrNull(id))
        goToUrl('season/manage', 'id=' + id);
}

function goToInsertSeason() {
    goToUrl('season/manage');
}

function goToSeasonEpisodePortal() {
    const sid = document.getElementById('selSeason').value;

    if (!isEmptyOrNull(sid))
        goToUrl('seasonepisode/portal', 'sid=' + sid);
}

function handleUps() {
    seasonUpsertLoader.loadElement();
}
