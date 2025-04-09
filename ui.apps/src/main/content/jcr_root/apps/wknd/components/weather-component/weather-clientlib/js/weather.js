"use strict";
$(document).ready(function() {
    $('#weatherForm').submit(function(event) {
        event.preventDefault();

        var cityName = $('#cityName').val();

        if (cityName) {
            console.log("City Name:", cityName);

            var servletUrl = "/bin/weatherapi"; 

            $.ajax({
                url: servletUrl,
                type: "POST",
                data: {
                    cityName: cityName
                },
                dataType: "json",
                success: function(response) {
                    console.log("Servlet Response:", response);

                    var responseContainer = $('#weatherResponse');
                    if (response && response.city && response.temperature) {
                        responseContainer.html(`
                            City: ${response.city}<br>
                            Temperature: ${response.temperature} F<br>
                            Wind Speed: ${response.windSpeed}<br>
                            Weather Condition: ${response.weatherCondition}<br>
                            Timestamp: ${response.timestamp}
                        `);
                    } else {
                        responseContainer.html("No weather data available.");
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Error calling servlet:", error);
                    $('#weatherResponse').html("Error fetching weather data.");
                }
            });
        } else {
            console.warn("No city name entered.");
        }
    });
});
