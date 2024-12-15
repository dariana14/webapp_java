(function () {
    'use strict';

    angular.module('app').factory('http', Service);

    function Service($http, $q, $log) {

        const POST = $http.post;
        const GET = $http.get;
        const DELETE = $http.delete;

        return {
            post: post,
            get: get,
            delete: del
        };

        function get(path) {
            return httpCommon(GET, path);
        }

        function del(path) {
            return httpCommon(DELETE, path);
        }

        function post(path, data) {
            return httpCommon(POST, path, data);
        }

        function httpCommon(method, path, data) {
            const url = path;

            $log.debug(methodToString(method) + ": " + url);
            if (method === POST) {
                $log.debug("data: " + angular.toJson(data));
            }

            const deferred = $q.defer();

            method(url, data).then(
                function(response) {
                    deferred.resolve(response.data);
                },
                function(response, status) {
                    deferred.reject(response);
                }
            );

            return deferred.promise;
        }

        function methodToString(method) {
            if (method === POST) {
                return 'POST';
            } else if (method === GET) {
                return 'GET';
            } else if (method === DELETE) {
                return 'DELETE';
            } else {
                throw 'unknown method';
            }
        }
    }

})();
