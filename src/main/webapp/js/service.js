/**
 * Created by oscar on 13/11/14.
 */

(function() {
    var agendaApp = angular.module('agendaApp', []);
    agendaApp.baseURI = 'http://localhost:8080/rest/people/';

    agendaApp.controller('ContactosCtrl', ['$scope', 'AgendaService', function ($scope, AgendaService) {
        var self = this;

        $scope.contacts = AgendaService.retrieveAll(
            function (response) {
                $scope.contacts = response.data.person;
            },
            function (response) {
                console.log(response);
            }
        );

        self.create = function (nombre, apellidos, nif) {
            AgendaService.create(nombre, apellidos, nif,
                function(response) {
                    AgendaService.retrieveAll(
                        function(response) {
                            $scope.contacts = response.data.person;
                        },
                        function(response) {
                            console.log(response);
                        }
                    );
                },
                function(response) {
                    console.log(response);
                }
            );
        };

        self.delete = function (nif) {
            AgendaService.delete(nif,
                function(response) {
                    AgendaService.retrieveAll(
                        function (response) {
                            $scope.contacts = response.data.person;
                        },
                        function (response) {
                            console.log(error);
                        }
                    );
                },
                function(response) {
                    console.log(response);
                }
            );
        }

        self.retreiveContact = function(nif) {
            AgendaService.retrieveContact(
                nif,
                function (response) {
                    $scope.currentContact = response.data;
                },
                function (response) {
                    console.log(response);
                });
        }

        self.update = function (person) {
            AgendaService.update(person,
                function (response) {
                    $scope.currentContact = response.data;
                    AgendaService.retrieveAll(
                        function (response) {
                            $scope.contacts = response.data.person;
                        },
                        function (response) {
                            console.log(response);
                        }
                    );
                },
                function (response) {
                    console.log(response);
                }
            );
        };

    }]);

    agendaApp.service('AgendaService', ['$http', function($http) {
        this.create = function(name, surname, nif, success, error) {
            dato = {'name': name, 'surname': surname, 'nif': nif};
            return $http.post(agendaApp.baseURI, dato)
                .then(success, error);
        }

        this.retrieveAll = function (success, error) {
            return $http.get(agendaApp.baseURI)
                .then(success, error);

        }

        this.retrieveContact = function(nif, success, error) {
            var url = agendaApp.baseURI + nif;
            return $http.get(url)
                .then(success, error);
        }

        this.delete = function(nif, success, error) {
            var url = agendaApp.baseURI + nif;
            var data = {'nif': nif}
            return $http.delete(url, data)
                .then(success, error);
        }

        this.update = function (person, success, error) {
            var url = agendaApp.baseURI + person.nif;
            return $http.put(url, person)
                .then(success, error);
        }
    }]);

})();