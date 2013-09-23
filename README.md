XWAF - X Web-application firewall
====

Web application firewall based on rules; Protected from popular web-vulnerabilities such as SQL-injection, Path traversal, etc;

Typicaly workflow scheme:
<img src="http://178.49.9.210/files/1032/img.png"/>

Parts of project:
<ul>
<li>1) Links extractor</li>
<li>2) Vulnerability detecter</li>
<li>3) Proxy filter</li>
</ul>
How it work?

<img src="http://178.49.9.210/files/1034/im2.jpg"/>

<ul>
<li>1) Links extractor - Takes a URL and tries to work around site recursively by links, keeping the values​of the potential vulnerability GET/POST/COOKIE parameters.</li>

<li>2) VulnerabilityDetecter - Using a list of links, trying to identify vulnerabilities options. This using time-based SQL-Injection attack
This application is multithreading and has CLI & GUI version;
Based logic of test suites:
<p>timeStart = time.now();</p>
<p>param="val ' AND SLEEP(15) -- -";</p>
<p>sendRequestToSite(URL + param);</p>
<p>timeEnd = time.now();</p>
<p>if (timeEnd - timeStart > 15) {</p>
<p>	print("SQL injection detected");</p>
<p>}</p>
</li>
<li>3) ProxyFilter - java proxy; Binds on localhost:8888 and check all request on vulnerability and block if detected attack.</li>
</ul>
