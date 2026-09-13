const seasonTeamOptionsLoader = new ElementLoader('selSeason', function () {
    const tid = document.getElementById('inpTeamId').value;

    return `/seasonteam/options?tid=${tid}`;
}, null);

const insertSeasonTeamLoader = new ElementLoader('divIns', function () {
    const tid = document.getElementById('inpTeamId').value;
    const sid = document.getElementById('selSeason').value;

    return !isEmptyOrNull(sid) ? `/seasonteam/insert?sid=${sid}&tid=${tid}` : null;
}, loadSeasonTeamOptions);

function loadSeasonTeamOptions() {
    seasonTeamOptionsLoader.loadElement();
}

function handleInsertSeasonTeam() {
    insertSeasonTeamLoader.loadElement();
}
