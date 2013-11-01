XWAF - X Web-application firewall
====

Web application firewall based on rules; Protected from popular web-vulnerabilities such as 
<ul>
<li>SQL-injection</li>
<li>Path traversal</li>
<li>NULL-byte injection</li>
<li>etc.</li>
</ul>

<h3>Typically firewall workflow scheme</h3>
<img src="http://178.49.9.210/files/1032/img.png"/>

<h3>Parts of project</h3>
<ul>
<li>1) Links extractor</li>
<li>2) Vulnerability detector</li>
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
<p>Sum is 8. If sum > 5 such request is blocked. Rules contains in SQLite database;</p>
<p>Also analyze http response. For example if page show SQL-error such as "You have an error in your SQL syntax; check the manual that corresponds to your 
MySQL server version for the right syntax to use near ''VALUE''')". This response not sends to client.</p>

<h3>Logger</h3>
<p>Every blocked requests stored in SQLite, and displays as html page. For example: </p>
<img src="http://178.49.9.210/files/1035/img1.png"/>

<h3>Blacklist/Whitelist</h3>
<p>SQLite database contains table blacklistIp; All ip clients with such IP address will be blocked.</p>
<p>To add IP-address into table use some SQLite manager, for example http://sqlitebrowser.sourceforge.net/; </p>
<br/>
<p>SQLite database contains table whitelist; You can insert into table part of url which cannot be analyse on vulnerabilities</p>
<p>For example: applications phpmyadmin, jira, redmine, etc, may generate false positives. For example see request for Show all entries in table in phpmyadmin</p>
```bash
POST http://127.0.0.1/tools/phpmyadmin/sql.php HTTP/1.1
Host: 127.0.0.1
Connection: keep-alive
Content-Type: application/x-www-form-urlencoded
Content-Length: 135

db=panel_prototype&table=domains&sql_query=SELECT+*+FROM+%60domains%60&pos=0&session_max_rows=all&goto=tbl_structure.php&navig=Show+all
```

<h3>Similar projects</h3>
We don't know combination of vulnerability scanner + WAF.
List of scanners &amp; WAFs;
<ul>
<li><a href="http://www-03.ibm.com/software/products/us/en/appscan/">AppScan</a>, <a href="http://www.ptsecurity.ru/xs7/">XSpider</a> - really cool scanners, but commercial</li>
<li><a href="https://github.com/nbs-system/naxsi/">Naxsi</a> - open source plugin for Nginx; rule-weight analysis</li>
<li><a href="http://guardian.jumperz.net/index.html">Guardian@JUMPERZ.NET</a> - open source WAF, rule-based analysis</li>
</ul>

<h3>Developers</h3>
Students of Novosibirs State University =)
