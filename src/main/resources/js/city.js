const cityListLoader = new ElementLoader('selCity', function () {
    return getOptionsUrl('city');
}, null);

const upsertCityLoader = new ElementLoader('inpId', function () {
    const id = document.getElementById('inpId').value;
    const nm = document.getElementById('nm').value;
    return `/city/upsert?nm=${nm}${!isEmptyOrNull(id) ? '&id=' + id : ''}`;
}, callBackUpsert);

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
