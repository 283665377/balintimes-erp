(function () {
    "use strict";

    angular.module('ngTreetable', [])

    /**
     * @ngdoc com.balintimes.erp.center.service
     */
        .factory('ngTreetableParams', ['$log', function ($log) {
            var params = function (baseConfiguration) {
                var self = this;

                /**
                 * @ngdoc method
                 * @param {<any>} parent A parent node to fetch children of, or null if fetching root nodes.
                 */
                this.getNodes = function (parent) {
                }

                /**
                 * @ngdoc method
                 * @param {<any>} node A node returned from getNodes
                 */
                this.getTemplate = function (node) {
                }

                /**
                 * @ngdoc property
                 */
                this.options = {};

                /**
                 * @ngdoc method
                 */
                this.refresh = function () {
                };

                this.expendNode = function (nodeid) {
                };


                if (angular.isObject(baseConfiguration)) {
                    angular.forEach(baseConfiguration, function (val, key) {
                        if (['getNodes', 'getTemplate', 'options'].indexOf(key) > -1) {
                            self[key] = val;
                        }
                        else {
                            $log.warn('ngTreetableParams - Ignoring unexpected property "' + key + '".');
                        }
                    });
                }

            }
            return params;
        }])

        .factory("tableParams", function () {
            return {ttNodeCounter: 0}
        })

        .controller('TreetableController', ['$scope', '$element', '$compile', '$templateCache', '$q', '$http', 'tableParams', function ($scope, $element, $compile, $templateCache, $q, $http, tableParams) {

            var params = $scope.ttParams;
            var table = $element;

            $scope.compileElement = function (node, parentId, parentNode) {
                var tpl = params.getTemplate(node);

                var templatePromise = $http.get(params.getTemplate(node), {cache: $templateCache}).then(function (result) {
                    return result.data;
                });

                return templatePromise.then(function (template) {
                    var template_scope = $scope.$parent.$new();
                    angular.extend(template_scope, {
                        node: node,
                        parentNode: parentNode
                    });
                    template_scope._ttParentId = parentId;
                    return $compile(template)(template_scope).get(0);
                })

            }

            /**
             * Expands the given node.
             * @param parentElement the parent node element, or null for the root
             * @param shouldExpand whether all descendants of `parentElement` should also be expanded
             */
            $scope.addChildren = function (parentElement, shouldExpand, expression) {
                var parentNode = parentElement ? parentElement.scope().node : null;
                var parentId = parentElement ? parentElement.data('ttId') : null;

                if (parentElement) {
                    parentElement.scope().loading = true;
                }

                var deferred = $q.defer();
                $q.when(params.getNodes(parentNode)).then(function (data) {
                    var elementPromises = [];
                    angular.forEach(data, function (node) {
                        elementPromises.push($scope.compileElement(node, parentId, parentNode));
                    });

                    $q.all(elementPromises).then(function (newElements) {
                        var parentTtNode = parentId != null ? table.treetable("node", parentId) : null;

                        $element.treetable('loadBranch', parentTtNode, newElements);

                        if (shouldExpand) {
                            angular.forEach(newElements, function (el) {
                                var d = $(el).scope().node;
                                //if(d[expression.key] != expression.value){
                                //    $scope.addChildren($(el), shouldExpand);
                                //}
                                var expand = expression ? (d[expression.key] != expression.value) : shouldExpand;
                                if (expand == true) {
                                    $scope.addChildren($(el), shouldExpand, expression);
                                }

                                //if(shouldExpand){
                                //    $scope.addChildren($(el), shouldExpand)
                                //}
                            });
                        }

                        if (parentElement) {
                            parentElement.scope().loading = false;
                        }

                        deferred.resolve(newElements);
                    });

                });

                return deferred.promise;


            }

            /**
             * Callback for onNodeExpand to add nodes.
             */
            $scope.onNodeExpand = function () {
                if (this.row.scope().loading) return; // make sure we're not already loading
                table.treetable('unloadBranch', this); // make sure we don't double-load
                $scope.addChildren(this.row, $scope.shouldExpand());
            }

            /**
             * Callback for onNodeCollapse to remove nodes.
             */
            $scope.onNodeCollapse = function () {
                if (this.row.scope().loading) return; // make sure we're not already loading
                table.treetable('unloadBranch', this);
            }

            /**
             * Rebuilds the entire table.
             */
            $scope.refresh = function (expression) {
                var rootNodes = table.data('treetable').nodes;
                while (rootNodes.length > 0) {
                    table.treetable('removeNode', rootNodes[0].id);
                }
                tableParams.ttNodeCounter = 0;

                return $scope.addChildren(null, expression ? true : $scope.shouldExpand(), expression);
            }

            // attach to params for convenience
            params.refresh = $scope.refresh;


            params.expendNode = function (nodeid) {
                table.treetable('expandNode', nodeid);
            };

            /**
             * Build options for the internal treetable library.
             */
            $scope.getOptions = function () {
                var opts = angular.extend({
                    expandable: true,
                    onNodeExpand: $scope.onNodeExpand,
                    onNodeCollapse: $scope.onNodeCollapse
                }, params.options);

                if (params.options) {
                    // Inject required event handlers before custom ones
                    angular.forEach(['onNodeCollapse', 'onNodeExpand'], function (event) {
                        if (params.options[event]) {
                            opts[event] = function () {
                                $scope[event].apply(this, arguments);
                                params.options[event].apply(this, arguments);
                            }
                        }
                    });
                }

                return opts;
            }

            $scope.shouldExpand = function () {
                return $scope.options.initialState === 'expanded';
            }

            $scope.options = $scope.getOptions();
            table.treetable($scope.options);
            $scope.addChildren(null, $scope.shouldExpand());

        }])

        .directive('ttTable', [function () {
            return {
                restrict: 'AC',
                scope: {
                    ttParams: '='
                },
                controller: 'TreetableController'
            }
        }])

        .directive('ttNode', ['tableParams', function (tableParams) {
            var ttNodeCounter = 0;
            return {
                restrict: 'AC',
                scope: {
                    isBranch: '=',
                    parent: '='
                },
                link: function (scope, element, attrs) {

                    var branch = angular.isDefined(scope.isBranch) ? scope.isBranch : true;

                    // Look for a parent set by the tt-tree directive if one isn't explicitly set
                    var parent = angular.isDefined(scope.parent) ? scope.parent : scope.$parent._ttParentId;

                    element.attr('data-tt-id', tableParams.ttNodeCounter++);
                    element.attr('data-tt-branch', branch);
                    element.attr('data-tt-parent-id', parent);
                }
            }

        }]);

})();
