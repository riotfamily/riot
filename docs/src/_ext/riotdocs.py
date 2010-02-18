# -*- coding: utf-8 -*-
"""
Sphinx plugins for Riot documentation.
"""
import re
import sphinx
from docutils import nodes
from sphinx.highlighting import lexers
from freemarker import FreeMarkerHtmlLexer

def setup(app):
    app.add_config_value('apirefs', {}, 'env')
    app.add_config_value('eval_conf_values', 'release html_title', 'env')
    
    app.connect('builder-inited', init)
    lexers['ftl'] = FreeMarkerHtmlLexer()
        
def init(app):
    cfg = app.config
    
    def resolve_conf_values(s, cfg):
        return re.sub('\$\{(.+?)\}', lambda m: cfg[m.group(1)], s)

    for key in cfg.eval_conf_values.split():
        cfg[key] = resolve_conf_values(cfg[key], cfg)
        
    def role(role, rawtext, text, lineno, inliner, options={}, content=[]):
        m = re.search('^(.*\.(.*?))(?:#(\S*))?(?:\s(.*))?$', text)
        (fqn, classname, method, label) = m.group(1, 2, 3, 4)
        
        path = fqn.replace('.', '/').replace('@', '')
        hash = ''
        if method:
            hash = '#' + method
            classname += '.' + method
        
        for lib, uri in cfg.apirefs.iteritems():
            if fqn.find(lib) != -1:
                uri = resolve_conf_values(uri, cfg)
                ref = '%s/index.html?%s%s' % (uri, path, hash)
                node = nodes.reference(rawtext, label or classname, refuri=ref, **options)
                node['classes'] += ['api', lib]
                return [node], []
    
    app.add_role('api', role)
