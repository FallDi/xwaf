XWAF - X Web-application firewall
====

Web application firewall based on rules; Protected from popular web-vulnerabilities such as 
<ul>
<li>SQL-injection</li>
<li>Path traversal</li>
<li>NULL-byte injection</li>
<li>etc</li>
</ul>

<h3>Typicaly firewall workflow scheme</h3>
<img src="http://178.49.9.210/files/1032/img.png"/>

<h3>Parts of project</h3>
<ul>
<li>1) Links extractor</li>
<li>2) Vulnerability detecter</li>
<li>3) Proxy filter</li>
</ul>


<h3>How it work?</h3>
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
In GUI version you can specify host and parameters, cookie and GET or POST method
<img src="http://178.49.9.210/files/1036/gui.png"/>
</li>
<li>3) ProxyFilter - java proxy; Binds on localhost:8888 and check all request on vulnerability and block if detected attack.
Also filter trying deobfuscation queries, for example <a href="http://en.wikipedia.org/wiki/Percent-encoding">persent-encoding</a></li>
</ul>

<h3>Rule-weight</h3>
<p>Every rule has weight, if URL contains a certain limit, the request is blocked.</p>
<p>For example http://178.49.9.210/daredevil/sql_inj/tester_login/<b>..</b>/login.php?<b>login[]</b>=l&password=<b>p' or 1 --</b></p>
<p><a href="https://www.owasp.org/index.php/Path_Traversal">Path traversal</a> <b>..</b> - 3 points</p>
<p><a href="http://hakipedia.com/index.php/Full_Path_Disclosure#Array.5B.5D_Parameter_Injection">Array[] Parameter injection</a> <b>login[]</b> - 2 point</p>
<p><a href="http://en.wikipedia.org/wiki/SQL_injection">SQL-injection</a> <b>password=p'or 1 --</b>  - 3 points</p>
Sum is 8. If sum > 5 such request is blocked.
Rules contains in SQLite database;

<h3>Logger</h3>
<p>Every blocked requests stored in SQLite, and displays as html page. For example: </p>
<img src="http://178.49.9.210/files/1035/img1.png"/>

<h3>Similar projects</h3>
<ul>
<li><a href="https://code.google.com/p/naxsi/">Naxsi</a></li>
<li><a href="http://guardian.jumperz.net/index.html">Guardian@JUMPERZ.NET</a></li>
</ul>
