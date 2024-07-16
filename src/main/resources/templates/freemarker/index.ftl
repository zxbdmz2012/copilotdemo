<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml"
      xmlns:o="urn:schemas-microsoft-com:office:office">

<head>
    <!-- NAME: NEW COLLECTION -->
    <!--[if gte mso 15]>
    <xml>
        <o:OfficeDocumentSettings>
            <o:AllowPNG/>
            <o:PixelsPerInch>96</o:PixelsPerInch>
        </o:OfficeDocumentSettings>
    </xml>
    <![endif]-->
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>*|MC:SUBJECT|*</title>

    <style type="text/css">
        p {
            margin: 10px 0;
            padding: 0;
        }

        table {
            border-collapse: collapse;
            border: 1px solid #fffef9;
        }

        th {
            font-weight: normal;
        }


        #bodyTable {
            /*@editable*/
            background-color: #E6A56A;
            /*@editable*/
            background-image: url(http://101.200.152.183:8080/static/bg.jpg);
            /*@editable*/
            background-repeat: no-repeat;
            /*@editable*/
            background-position: center;
            /*@editable*/
            background-size: cover;
            /*@editable*/
            border-top: 0;
            /*@editable*/
            border-bottom: 0;
        }
    </style>
</head>

<body>
<h3>这是${taskName}，对应的自动化平台测试数据分析结果</h3>
<table align="center" border="0" cellpadding="0" height="100%" width="100%" id="bodyTable" style="color: #fff;">
    <tbody>
    <tr align="center" height="50px" width="100%" style="color: #fff;font-size: 30px">
        <td>
            <a
                    style="font-weight: bold;letter-spacing: normal;line-height: 100%;text-align: center;text-decoration: none;color: #FFFFFF;font-size: 30px;">自动化测试平台
            </a>
        </td>
    </tr>
    <tr style="font-size: 18px;">
        <td>
            <a
                    style="font-weight: bold;letter-spacing: normal;line-height: 100%;text-align: center;text-decoration: none;color: #fdb933;font-size: 18px;margin-left: 70px;">总计分析：
            </a>
        </td>
    </tr>
    <tr height="8px">
    </tr>
    <tr>
        <td>
            <table align="center" border="1px" cellpadding="1" height="90%" width="90%" style="color: #fff;">
                <tr align="center">
                    <th>序号</th>
                    <th>总用例数</th>
                    <th>通过数</th>
                    <th>失败数</th>
                    <th>跳过数</th>
                    <th>通过率</th>
                    <th>失败率</th>
                    <th>跳过率</th>
                    <th>总和</th>
                </tr>
                <tr align="center">
                    <td>1</td>
                    <td>${globalObj.caseSummaryNum}</td>
                    <td>${globalObj.passNum}</td>
                    <td>${globalObj.failNum}</td>
                    <td>${globalObj.blockNum}</td>
                    <td>${globalObj.passRatio}</td>
                    <td>${globalObj.failRatio}</td>
                    <td>${globalObj.blockRatio}</td>
                    <td>100%</td>
                </tr>

            </table>
        </td>
    </tr>

    <tr height="30px">
        <td></td>
    </tr>
    <tr style="font-size: 18px">
        <td>
            <a
                    style="font-weight: bold;letter-spacing: normal;line-height: 100%;text-align: center;text-decoration: none;color: #fdb933;font-size: 18px;margin-left: 70px;">黄金流分析：
            </a>
        </td>
    </tr>
    <tr height="8px">
    </tr>
    <tr>
        <td>
            <table align="center" border="1" cellpadding="0" height="90%" width="90%" style="color: #fff;">
                <tr align="center">
                    <th>序号</th>
                    <th>总用例数</th>
                    <th>通过数</th>
                    <th>失败数</th>
                    <th>跳过数</th>
                    <th>通过率</th>
                    <th>失败率</th>
                    <th>跳过滤</th>
                    <th>总和</th>
                </tr>
                <tr align="center">
                    <td>1</td>
                    <td>${goldObj.caseSummaryNum}</td>
                    <td>${goldObj.passNum}</td>
                    <td>${goldObj.failNum}</td>
                    <td>${goldObj.blockNum}</td>
                    <td>${goldObj.passRatio}</td>
                    <td>${goldObj.failRatio}</td>
                    <td>${goldObj.blockRatio}</td>
                    <td>100%</td>
                </tr>

            </table>
        </td>
    </tr>
    <tr height="30px">
        <td></td>
    </tr>
    <tr style="font-size: 18px;font-weight: bold;font-family: Arial;">
        <td>
            <a
                    style="font-weight: bold;letter-spacing: normal;line-height: 100%;text-align: center;text-decoration: none;color: #fdb933;font-size: 18px;margin-left: 70px;">支付分析：
            </a>
        </td>
    </tr>
    <tr height="8px">
    </tr>
    <tr>
        <td>
            <table align="center" border="1" cellpadding="0" height="90%" width="90%" style="color: #fff;">
                <tr align="center">
                    <th>序号</th>
                    <th>总用例数</th>
                    <th>通过数</th>
                    <th>失败数</th>
                    <th>跳过数</th>
                    <th>通过率</th>
                    <th>失败率</th>
                    <th>跳过滤</th>
                    <th>总和</th>
                </tr>
                <tr align="center">
                    <td>1</td>
                    <td>${payObj.caseSummaryNum}</td>
                    <td>${payObj.passNum}</td>
                    <td>${payObj.failNum}</td>
                    <td>${payObj.blockNum}</td>
                    <td>${payObj.passRatio}</td>
                    <td>${payObj.failRatio}</td>
                    <td>${payObj.blockRatio}</td>
                    <td>100%</td>
                </tr>
            </table>
        </td>
    </tr>
    <tr height="10px">
    </tr>
    <tr align="center" style="font-size: 20px;">
        <td align="center" valign="middle" style="font-family: Arial; font-size: 18px; padding: 20px">
            <table border="0" cellpadding="0" cellspacing="0" class="mcnButtonContentContainer"
                   style="border-collapse: separate !important;border-radius: 3px;background-color: #ED5A2E;">
                <tbody>
                <tr>
                    <td align="center" valign="middle" style="font-family: Arial; font-size: 16px; padding: 10px;">
                        <a class="mcnButton " title="点击进入看板" href="#" target="_blank"
                           style="font-weight: bold;letter-spacing: normal;line-height: 100%;text-align: center;text-decoration: none;color: #FFFFFF;">进入看板</a>
                    </td>
                </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <tr height="10px">
    </tbody>

</table>
</body>

</html>
