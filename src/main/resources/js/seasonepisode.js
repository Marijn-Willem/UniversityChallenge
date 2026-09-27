const dateRegex = /\d{8}/;

const seasonEpisodeOptionsLoader = new ElementLoader('selEpId', function () {
    return getLoaderUrl('options')
}, null);

const insertSeasonEpisodeLoader = new ElementLoader('divIns', function () {
    return getLoaderUrl('insert')
}, loadSeasonEpisodeOptions);

const updateLoader = new ElementLoader('divUpd', function () {
    const sid = document.getElementById('sid').value;
    const eid = document.getElementById('eid').value;

    const t1id = document.getElementById('t1id').value;
    const t2id = document.getElementById('t2id').value;
    const s1 = document.getElementById('s1').value;
    const s2 = document.getElementById('s2').value;
    const dt = document.getElementById('dt').value;

    if (doCheckAndAlert(checkNumericOrEmpty(s1), 'Invalid numeric value score 1') &&
        doCheckAndAlert(checkNumericOrEmpty(s2), 'Invalid numeric value score 2') &&
        doCheckAndAlert(checkDateOrEmpty(dt), 'Invalid date')) {
        let url = `/seasonepisode/update?sid=${sid}&eid=${eid}`;
        url = appendIfNonEmpty(url, 't1id', t1id);
        url = appendIfNonEmpty(url, 't2id', t2id);
        url = appendIfNonEmpty(url, 's1', s1);
        url = appendIfNonEmpty(url, 's2', s2);
        url = appendIfNonEmpty(url, 'dt', dt);

        return url;
    }

    return null;
}, null);

function loadSeasonEpisodeOptions() {
    seasonEpisodeOptionsLoader.loadElement();
}

function insertSeasonEpisodes() {
    insertSeasonEpisodeLoader.loadElement();
}

function goToManageSeasonEpisode() {
    const sid = document.getElementById('inpSeasId').value;
    const eid = document.getElementById('selEpId').value;

    goToUrl('seasonepisode/manage', `sid=${sid}&eid=${eid}`);
}

function handleUpd() {
    updateLoader.loadElement();
}

function getLoaderUrl(specificPart) {
    const sid = document.getElementById('inpSeasId').value;
    const eid = document.getElementById('inpEpId').value;
    
    let url = `/seasonepisode/${specificPart}?sid=${sid}`;
    url = appendIfNonEmpty(url, 'eid', eid);
    
    return url;
}

function checkNumericOrEmpty(str) {
    return isEmptyOrNull(str) || !isNaN(parseInt(str));
}

function checkDateOrEmpty(str) {
    return isEmptyOrNull(str) || dateRegex.test(str);
}

function doCheckAndAlert(check, errorMessage) {
    if (!check)
        alert(errorMessage);

    return check;
}
