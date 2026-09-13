function ElementLoader(elId, getUrl, callBack) {
    this.loadElement = function () {
        const url = getUrl();

        if (url !== null) {
            const xHttp = new XMLHttpRequest();
            xHttp.onreadystatechange = function () {
                if (this.readyState === 4 && this.status === 200) {
                    if (elId !== null)
                        document.getElementById(elId).innerHTML = this.responseText;

                    if (callBack !== null)
                        callBack();
                }
            };

            xHttp.open("GET", url, true);
            xHttp.send();
        }
    }
}

function callBackUpsert() {
    const inpId = document.getElementById('inpId');
    const elButUps = document.getElementById('butUps');

    inpId.value = inpId.innerText;
    document.getElementById('divUps').innerText = `${elButUps.value} successful`;
    elButUps.value = 'Update';
}

function isEmptyOrNull(el) {
    return el === undefined || el === null || el === '';
}

function goToUrl(url, parameters) {
    window.location.href = '/' + url + (!isEmptyOrNull(parameters) ? '?' + parameters : '');
}
