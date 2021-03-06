package com.athaydes.spockframework.report.internal

import groovy.util.logging.Log
import groovy.xml.MarkupBuilder

import java.util.logging.Level

import com.athaydes.spockframework.report.SpockReportExtension

/**
 *
 * User: Renato
 */
@Log
abstract class AbstractHtmlCreator<T> {

	String css
	String outputDir
	boolean hideEmptyBlocks = false
	KnowsWhenAndWhoRanTest whenAndWho = new KnowsWhenAndWhoRanTest()
	String excludeToc = "false"

	void setCss( String css ) {
		if ( !css || css.trim().empty ) return
		def cssResource = this.class.getResource( "/$css" )
		if ( cssResource )
			try {
				this.@css = cssResource.text
			} catch ( e ) {
				log.log(Level.FINE, "${this.class.name}: Failed to set CSS file to $css", e)
			}
		else
			log.fine "${this.class.name}: The CSS file does not exist: ${css}"
	}

	String reportFor( T data ) {
		def writer = new StringWriter()
		def builder = new MarkupBuilder( new IndentPrinter( new PrintWriter( writer ), "" ) )
		builder.expandEmptyElements = true
		builder.html {
			head {
				meta('http-equiv':'Content-Type', content:'text/html; charset=utf-8')
				if ( css ) style css
			}
			body {
				h2 reportHeader( data )
				hr()
				writeSummary( builder, data )
				writeDetails( builder, data )
				hr()
				writeFooter( builder )
			}
		}
		'<!DOCTYPE html>' + writer.toString()
	}

	protected void writeFooter( MarkupBuilder builder ) {
		builder.div( 'class': 'footer' ) {
			mkp.yieldUnescaped(
					"Generated by <a href='${SpockReportExtension.PROJECT_URL}'>Athaydes Spock Reports</a>" )
		}
	}

    abstract protected String reportHeader( T data )

	abstract protected void writeSummary( MarkupBuilder builder, T data )

	abstract protected void writeDetails( MarkupBuilder builder, T data )

}
