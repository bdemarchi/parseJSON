# parseJSON
OpenOffice plugin to parse JSON of URL

This plugin was created originally in order to access coinmarket API.
OpenOffice version 4.1.1, which was the version available at the date this plugin was created, had no function for that purpose.

This plugin adds a function called PARSEJSON(url, query) to Calc.
url - can be http or https 
query - similar to XPath query

In order to parse the JSON and query its values the plugin uses the project https://github.com/json-path/JsonPath version 2.3.0.
Check the FsonPath project in order to understand how to query values in the JSON.

Fucntio example:
=PARSEJSON("https://api.coinmarketcap.com/v1/ticker/bitcoin/";"$[0]price_usd")


