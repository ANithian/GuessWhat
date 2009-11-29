# Go to http://wiki.merbivore.com/pages/init-rb

#  use_orm :none
use_test :rspec
use_template_engine :erb
# Load the apis
ENV['APPLICATION_ROOT'] = File.join(File.dirname(__FILE__), '..')
require 'appengine-apis/local_boot'
require 'set'
# Specify a specific version of a dependency
# dependency "RedCloth", "> 3.0"

Merb::BootLoader.before_app_loads do
   
end
 
Merb::BootLoader.after_app_loads do
  # This will get executed after your app's classes have been loaded.
#  Merb::MemcacheSession.store = AppEngine::Memcache.new
#  module AppEngine
#    # For the memcache-client gem.
#    class Memcache
#      include Merb::MemcacheStore
#    end
#  end
  CACHE = AppEngine::Memcache.new
  
  #Administrator list. Pages such as my_questions will not only show
  #questions affiliated with the admin but all users too.
  ADMINSET = Set.new
  ADMINSET.add(100000289421407) #Amit Test account
#  ADMINSET.add(6217743)
end

# Move this to application.rb if you want it to be reloadable in dev mode.
Merb::Router.prepare do
  match('/').to(:controller => "guesswhat", :action =>'index')

  default_routes
end

Merb::Config.use { |c|
  c[:environment]         = 'production',
  c[:framework]           = {},
  c[:log_level]           = :debug,
  c[:log_stream]          = STDOUT,
  # or use file for logging:
  # c[:log_file]          = Merb.root / "log" / "merb.log",
  c[:use_mutex]           = false,
  c[:session_store]       = 'cookie',
  c[:session_id_key]      = '_guesswhat_session_id',
  c[:session_secret_key]  = 'ff2325bd7fb5107d241b5dc854bb266dfaa30f3d',
  c[:exception_details]   = true,
  c[:reload_classes]      = false,
  c[:reload_templates]    = false,
  c[:reload_time]         = 0.5
}
