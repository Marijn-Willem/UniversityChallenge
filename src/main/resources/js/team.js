const teamListLoader = new ElementLoader('selTeam', function () {
    return '/team/options';
}, null);

const upsertTeamLoader = new ElementLoader('inpId', function () {
    const id = document.getElementById('inpId').value;
    const nm = document.getElementById('nm').value;
    const cid = document.getElementById('cid').value;

    if (!isEmptyOrNull(cid))
        return `/team/upsert?nm=${nm}&cid=${cid}${!isEmptyOrNull(id) ? '&id=' + id : ''}`;
}, callBackUpsert);

function loadTeamList() {
    teamListLoader.loadElement();
}

function goToManageTeam() {
    const id = document.getElementById('selTeam').value;
    if (!isEmptyOrNull(id))
        goToUrl('team/manage', 'id=' + id);
}

function goToInsertTeam() {
    goToUrl('team/manage')
}

function goToSeasonTeamPortal() {
    const tid = document.getElementById('selTeam').value;
    if (!isEmptyOrNull(tid))
        goToUrl('seasonteam/portal', 'tid=' + tid);
}

function handleUps() {
    upsertTeamLoader.loadElement();
}
