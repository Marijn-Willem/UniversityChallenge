const cityListLoader = new ElementLoader('selCity', function () {
    return '/city/options';
}, null);

const upsertCityLoader = new ElementLoader('inpId', function () {
    const id = document.getElementById('inpId').value;
    const nm = document.getElementById('nm').value;
    return '/city/upsert?nm=' + nm + (!isEmptyOrNull(id) ? '&id=' + id : '');
}, function () {
    const inpId = document.getElementById('inpId');
    const elButUps = document.getElementById('butUps');

    inpId.value = inpId.innerText;
    document.getElementById('divUps').innerText = `${elButUps.value} successful`;
    elButUps.value = 'Update';
});

function loadCityList() {
    cityListLoader.loadElement();
}

function goToManageCity() {
    const id = document.getElementById('selCity').value;
    if (!isEmptyOrNull(id))
        goToUrl('city/manage', 'id=' + id);
}

function goToInsertCity() {
    goToUrl('city/manage')
}

function handleUps() {
    upsertCityLoader.loadElement();
}
