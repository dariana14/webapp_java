(function () {
    'use strict';

    const messages = {
        'NotNull.order.orderNumber' : 'Order number is missing',
        'Size.order.orderNumber' : 'Order number must be between {2} and {50} characters',
    };

    angular.module('app').service('messageService', Srv);

    function Srv() {

        this.getMessage = getMessage;

        function getMessage(key, params) {
            let text = messages[key];

            if (text === undefined) {
                return key;
            }

            if (params === undefined) {
                return text;
            }

            for (let i = 0; i < params.length; i++) {
                text = text.replace(new RegExp('\\{' + (i + 1) + '\\}', 'g'), params[i]);
            }

            return text;
        }

    }

})();
