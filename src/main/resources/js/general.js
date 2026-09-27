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

    const isInsert = isEmptyOrNull(inpId.value);
    document.getElementById('divUps').innerText = `${elButUps.value} successful`;

    if (isInsert) {
        elButUps.value = 'Update';
        const id = inpId.innerText
        inpId.value = id;
        returnPath = appendIfNonEmpty(returnPath, 'id', id);
    }
}

function getOptionsUrl(entityName) {
    let url = `/${entityName}/options`;
    url = appendIfNonEmpty(url, 'id', document.getElementById('inpId').value);

    return url;
}

function isEmptyOrNull(el) {
    return el === undefined || el === null || el === '';
}

function appendIfNonEmpty(url, param, str) {
    const delim = url.indexOf('?') >= 0 ? '&' : '?';

    return `${url}${!isEmptyOrNull(str) ? `${delim}${param}=${str}` : ''}`;
}

function goToUrl(url, parameters) {
    window.location.href = '/' + url + (!isEmptyOrNull(parameters) ? '?' + parameters : '');
}
