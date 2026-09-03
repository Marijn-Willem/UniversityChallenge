const cityListLoader = new ElementLoader('selCity', function () {
    return '/city/options';
}, null);

const insertCityLoader = new ElementLoader('divIns', function () {
    const nm = document.getElementById('nm').value;
    return '/city/insert?nm=' + nm;
}, function () { loadCityList(); });

function loadCityList() {
    cityListLoader.loadElement();
}

function insertCity() {
    insertCityLoader.loadElement();
}
