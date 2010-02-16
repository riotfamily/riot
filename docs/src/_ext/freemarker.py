# -*- coding: utf-8 -*-

from pygments.lexer import DelegatingLexer, RegexLexer, bygroups
from pygments.lexers.web import HtmlLexer
from pygments.token import Punctuation, Text, Comment, Name, String, Other

class FreeMarkerHtmlLexer(DelegatingLexer):
    def __init__(self, **options):
        super(FreeMarkerHtmlLexer, self).__init__(HtmlLexer, FreeMarkerLexer, **options)
        
class FreeMarkerLexer(RegexLexer):
    """
    Generic `FreeMarker <http://www.freemarker.org/>`_ template lexer.

    It just highlights FreeMarker code between the preprocessor directives,
    other data is left untouched by the lexer.
    """

    name = 'FreeMarker'
    aliases = ['freemarker', 'ftl']
    mimetypes = ['application/x-freemarker', 'application/x-ftl']

    tokens = {
        'root': [
            (r'(?s)<#--.*?-->', Comment.Multiline),
            (r'(<#)(\S+)', bygroups(Comment.Preproc, Name.Builtin), 'directive'),
            (r'(<@)(\S+)', bygroups(Comment.Preproc, Name.Function), 'macro'),
            (r'(</#)(.*?)(>)', bygroups(Comment.Preproc, Name.Builtin, Comment.Preproc)), # </#if>
            (r'(</@)(.*?)(>)', bygroups(Comment.Preproc, Name.Function, Comment.Preproc)), # </@foo.bar>
            (r'\$\{', Comment.Preproc, 'expression'),
            (r'.+?(?=\$\{)', Other),
            (r'.+?(?=<)', Other),
            (r'.+?(?=$)', Other),
        ],
        'expression': [
            (r'\}', Comment.Preproc, '#pop'),
            (r'[^}]+', Name.Function),
        ],
        'directive': [
            (r'/?\s*>', Comment.Preproc, '#pop'),
            (r'[^>]*', Text),
        ],
        'macro': [
            (r'(\s?=\s?)([^>\s]+)', bygroups(Punctuation, String.Single)),
            (r'>', Comment.Preproc, '#pop'),
            (r'[^>=]*', Text),
        ]
    }