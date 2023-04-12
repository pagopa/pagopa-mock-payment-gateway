function changeRequestTextarea(requestType) {
    switch (requestType) {
        case 'payment':
            document.getElementById('requestTextarea').value = '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:pp="http://adapter.p2b.sia.eu/srv/pp">\n	<soapenv:Header/>\n	<soapenv:Body>\n		<pp:inserimentoRichiestaPagamentoPagoPa>\n			<arg0>\n				<contesto>\n					<guid>inserimentoRichiestaPagamentoPagoPa</guid>\n					<token>XB8YaVD0V75QxJnPBF9A2nN1g52Ne6IdnPbAjbXd1yI1PaI7RF</token>\n					<utenteAttivo>\n						<codGruppo>03069</codGruppo>\n						<codIstituto>03069</codIstituto>\n						<codUtente>MERCHRIKICANALE</codUtente>\n					</utenteAttivo>\n				</contesto>\n				<richiestaPagamentoPagoPa>\n					<tag>HA334ZP</tag>\n					<importo>5</importo>\n					<causale>testpagopa1</causale>\n					<numeroTelefonicoCriptato>EHPYPUp2R4T2r7ldm+CB4w==</numeroTelefonicoCriptato>\n					<idPSP>20</idPSP>\n					<idPagoPa>23</idPagoPa>\n				</richiestaPagamentoPagoPa>\n			</arg0>\n		</pp:inserimentoRichiestaPagamentoPagoPa>\n	</soapenv:Body>\n</soapenv:Envelope>'
            break
        case 'inquiry':
            document.getElementById('requestTextarea').value = '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:pp="http://adapter.p2b.sia.eu/srv/pp">\n	<soapenv:Header/>\n	<soapenv:Body>\n		<pp:inquiryTransactionStatus>\n			<arg0>\n				<contesto>\n					<guid>inquiryTransactionStatus</guid>\n					<token>XB8YaVD0V75QxJnPBF9A2nN1g52Ne6IdnPbAjbXd1yI1PaI7RF</token>\n					<utenteAttivo>\n						<codGruppo>03069</codGruppo>\n						<codIstituto>03069</codIstituto>\n						<codUtente>TESTTERMINAL1</codUtente>\n					</utenteAttivo>\n				</contesto>\n				<correlationId>bf6b1ac5-932f-485c-84c8-ecef7ac26461</correlationId>\n				<idPagoPa>23</idPagoPa>\n			</arg0>\n		</pp:inquiryTransactionStatus>\n	</soapenv:Body>\n</soapenv:Envelope>'
            break
        case 'refund':
            document.getElementById('requestTextarea').value = '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:pp="http://adapter.p2b.sia.eu/srv/pp">\n	<soapenv:Header/>\n	<soapenv:Body>\n		<pp:stornoPagamento>\n			<arg0>\n				<contesto>\n					<guid>stornoPagamento</guid>\n					<token>XB8YaVD0V75QxJnPBF9A2nN1g52Ne6IdnPbAjbXd1yI1PaI7RF</token>\n					<utenteAttivo>\n						<codGruppo>03069</codGruppo>\n						<codIstituto>03069</codIstituto>\n						<codUtente>TESTTERMINAL1</codUtente>\n					</utenteAttivo>\n				</contesto>		 \n				<idPagoPa>23</idPagoPa>\n				<causale>prova</causale>\n				<tipoStorno>01</tipoStorno>\n			</arg0>\n		</pp:stornoPagamento>\n	</soapenv:Body>\n</soapenv:Envelope>'
            break
        default:
            break
    }
}