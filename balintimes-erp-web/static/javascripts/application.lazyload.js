/**
 * Created by AlexXie on 2015/8/26.
 */
'use strict';

angular.module('app')
    .constant('JQ_CONFIG', {
        easyPieChart: ['/bower_components/bower-jquery-easyPieChart/dist/jquery.easypiechart.min.js']
    })
    .config(['$ocLazyLoadProvider',
        function ($ocLazyLoadProvider) {
            $ocLazyLoadProvider.config({
                debug: false,
                events: true,
                modules: [{
                    name: 'BMM_Line_Service',
                    files:["/pages/bmms/base/line/line.client.service.js"]
                }]
            });
        }]);