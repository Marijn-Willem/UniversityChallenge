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
