function pricemap() {
    var pricemapData = generate(50000);
    var leveledData = levels(pricemapData);
    var diagram = voronoi(leveledData);
}

function voronoi(leveledData) {
    var t0 = performance.now();
    var voronoi = new Voronoi();
    var diagram = voronoi.compute(sites, bbox);
    var t1 = performance.now();
    console.log("voronoi diagram generation took " + (t1 - t0) + " milliseconds.")
}

function levels(pricemapData) {
    var result = [];
    var t0 = performance.now();
    grid(pricemapData);
    var t1 = performance.now();
    console.log("leveling data took " + (t1 - t0) + " milliseconds.")
    return result;

    function grid(pricemapData) {
        var grid = new Array();
        pricemapData.forEach(function(item) {
            var latIndex = Math.floor((item.lat - minLat) / windowSize);
            var latSlot = grid[latIndex];
            if (typeof latSlot == "undefined") {
                latSlot = new Array();
                grid[latIndex] = latSlot;
            }
            var lngIndex = Math.floor((item.lng - minLng) / windowSize);
            var lngSlot = latSlot[lngIndex];
            if (typeof lngSlot == "undefined") {
                lngSlot = [];
                latSlot[lngIndex] = lngSlot;
            }
            lngSlot.push(item);
        });
        pricemapData.forEach(function(item) {
            result.push({lat: item.lat, lng: item.lng, value: averageValue(item)});
        });

        function averageValue(one) {
            var sum = one.value;
            var count = 1;
            var latSlot = grid[latIndex];
            for (i = -1; i < 2; i++) {
                var latIndex = Math.floor((one.lat - minLat) / windowSize);
                var latSlot = grid[latIndex + i];
                if (typeof latSlot == "undefined") {
                    continue;
                }
                for (j = -1; j < 2; j++) {
                    var lngIndex = Math.floor((one.lng - minLng) / windowSize);
                    var lngSlot = latSlot[lngIndex + j];
                    if (typeof lngSlot == "undefined") {
                        continue;
                    }
                    lngSlot.forEach(function(item) {
                        if (Math.abs(one.lat - item.lat) < windowSize && Math.abs(one.lng - item.lng) < windowSize) {
                            sum += item.value;
                            count++;
                        }
                    });
                }
            }
            return sum / count;
        }
    }

    function quadratic(pricemapData) {
        pricemapData.forEach(function(item) {
            result.push({lat: item.lat, lng: item.lng, value: averageValue(item, pricemapData)});
        });

        function averageValue(one, all) {
            var sum = one.value;
            var count = 1;
            all.forEach(function(item) {
                if (Math.abs(one.lat - item.lat) < windowSize && Math.abs(one.lng - item.lng) < windowSize) {
                    sum += item.value;
                    count++;
                }
            });
            return sum / count;
        }
    }
}

var windowSize = 0.06;
var minLat = 49.0756565;
var maxLat = 50.5550039;
var minLng = 13.305744;
var maxLng = 16.1995283;

function generate(count) {
    function randomLat() {
        return Math.random() * (maxLat - minLat) + minLat;
    }

    function randomLng() {
        return Math.random() * (maxLng - minLng) + minLng;
    }

    function randomValue() {
        return Math.random() * 100;
    }

    var result = [];
    var t0 = performance.now();
    for (i = 0; i < count; i++) {
        result.push({lat: randomLat(), lng: randomLng(), value: randomValue()});
    }
    var t1 = performance.now();
    console.log("generating random data took " + (t1 - t0) + " milliseconds.")
    return result;
}